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
import org.springframework.util.Assert;
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
        // request에서 parameter를 가져오기
        MultiValueMap<String, String> params = OAuth2AuthorizationResponseUtils.toMultiMap(request.getParameterMap());
        if (!OAuth2AuthorizationResponseUtils.isAuthorizationResponse(params)) {
            throw new OAuth2AuthenticationException();
        }

        // session에서 authorizationRequest를 가져오기
        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository.removeAuthorizationRequest(
                request, response);
        if (authorizationRequest == null) {
            throw new OAuth2AuthenticationException();
        }

        // registrationId를 가져오고 clientRegistration을 가져오기
        String registrationId = getRegistrationId(request);
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new OAuth2AuthenticationException();
        }

        // code를 포함한 authorization response를 객체로 가져오기
        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponseUtils.convert(params,
                clientRegistration.redirectUri());

        // access token을 가져오기 위한 request 객체 만들기
        OAuth2LoginAuthenticationToken authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration,
                new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse));

        // OAuth2LoginAuthenticationToken 만들기
        OAuth2LoginAuthenticationToken authenticationResult = (OAuth2LoginAuthenticationToken) getAuthenticationManager()
                .authenticate(authenticationRequest);

        // provider 인증 후 authenticated된 OAuth2AuthenticationToken 객체 가져오기
        OAuth2AuthenticationToken oauth2Authentication = this.authenticationResultConverter.convert(
                authenticationResult);
        Assert.notNull(oauth2Authentication, "authentication result cannot be null");

        // authorizedClientRepository 에 저장할 OAuth2AuthorizedClient을 만들고 저장
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
                authenticationResult.getClientRegistration(), oauth2Authentication.getName(),
                authenticationResult.getAccessToken());

        this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, oauth2Authentication, request, response);

        return oauth2Authentication;
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

        successHandler.onAuthenticationSuccess(request, response, authResult);
    }
}
