package nextstep.security.oauth2.client.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import nextstep.security.access.RequestMatcher;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.oauth2.client.AuthorizationRequestRepository;
import nextstep.security.oauth2.client.HttpSessionOAuth2AuthorizationRequestRepository;
import nextstep.security.oauth2.client.OAuth2AuthorizedClient;
import nextstep.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import nextstep.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.client.registration.ClientRegistrationRepository;
import nextstep.security.oauth2.core.OAuth2AuthenticationException;
import nextstep.security.oauth2.core.OAuth2AuthorizationExchange;
import nextstep.security.oauth2.core.OAuth2AuthorizationRequest;
import nextstep.security.oauth2.core.OAuth2AuthorizationResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.log.LogMessage;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.GenericFilterBean;

public class OAuth2LoginAuthenticationFilter extends GenericFilterBean {
    public static final String DEFAULT_FILTER_PROCESSES_URI = "/login/oauth2/code/";

    private final AuthorizationRequestRepository authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();

    private final Converter<OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken> authenticationResultConverter = this::createAuthenticationResult;

    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final RequestMatcher requiresAuthenticationRequestMatcher;

    private final AuthenticationManager authenticationManager;

    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    private static final AuthenticationSuccessHandler successHandler = (request, response, authentication) -> response.sendRedirect(
            "/");

    public OAuth2LoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
                                           OAuth2AuthorizedClientRepository authorizedClientRepository,
                                           AuthenticationManager authenticationManager) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.requiresAuthenticationRequestMatcher = request -> {
            String uri = request.getRequestURI();
            return uri.startsWith(DEFAULT_FILTER_PROCESSES_URI);
        };
        this.authorizedClientRepository = authorizedClientRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!requiresAuthentication(request)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            Authentication authenticationResult = attemptAuthentication(request, response);
            if (authenticationResult == null) {
                return;
            }
            successfulAuthentication(request, response, authenticationResult);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
        }
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        // 1. 요청 파라미터에서 필요한 정보를 추출
        MultiValueMap<String, String> params = OAuth2AuthorizationResponseUtils.toMultiMap(request.getParameterMap());
        if (!OAuth2AuthorizationResponseUtils.isAuthorizationResponse(params)) {
            throw new OAuth2AuthenticationException("Invalid OAuth2 Authorization Response");
        }

        // 2. 세션에서 OAuth2AuthorizationRequest 로드
        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository.removeAuthorizationRequest(
                request, response);
        if (authorizationRequest == null) {
            throw new OAuth2AuthenticationException("Authorization Request not found in session");
        }

        // 3. registrationId와 ClientRegistration 조회
        String registrationId = getRegistrationId(request);
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new OAuth2AuthenticationException("Client Registration not found");
        }

        // 4. 인증에 필요한 OAuth2AuthorizationResponse 생성
        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponseUtils.convert(params,
                clientRegistration.redirectUri());

        // 5. Authentication 생성 (실제 인증은 Provider가 수행)
        Authentication authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration,
                new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse));

        // 6. AuthenticationManager에 요청 전달
        return getAuthenticationManager().authenticate(authenticationRequest);
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        if (this.requiresAuthenticationRequestMatcher.matches(request)) {
            return true;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger
                    .trace(LogMessage.format("Did not match request to %s", this.requiresAuthenticationRequestMatcher));
        }
        return false;
    }

    private AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    private String getRegistrationId(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith(DEFAULT_FILTER_PROCESSES_URI)) {
            return uri.substring(DEFAULT_FILTER_PROCESSES_URI.length());
        }

        return null;
    }

    private OAuth2AuthenticationToken createAuthenticationResult(OAuth2LoginAuthenticationToken authenticationResult) {
        return new OAuth2AuthenticationToken(authenticationResult.getPrincipal(),
                authenticationResult.getAuthorities());
    }

    private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                          Authentication authResult) throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        if (authResult instanceof OAuth2LoginAuthenticationToken authentication) {
            OAuth2AuthenticationToken oauth2Authentication = this.authenticationResultConverter.convert(
                    authentication);

            // authorizedClientRepository 에 저장할 OAuth2AuthorizedClient을 만들고 저장
            OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
                    authentication.getClientRegistration(),
                    oauth2Authentication.getName(),
                    authentication.getAccessToken());

            this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, oauth2Authentication, request,
                    response);
        }

        successHandler.onAuthenticationSuccess(request, response, authResult);
    }
}
