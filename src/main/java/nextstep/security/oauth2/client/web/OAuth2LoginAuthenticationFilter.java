package nextstep.security.oauth2.client.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.ProviderManager;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import nextstep.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.client.registration.ClientRegistrationRepository;
import nextstep.security.oauth2.client.userinfo.OAuth2UserService;
import nextstep.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import nextstep.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import nextstep.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import nextstep.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

public class OAuth2LoginAuthenticationFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginAuthenticationFilter.class);

    private static final String LOGIN_CALL_BACK_URI = "/login/oauth2/code/";

    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthorizationRequestRepository authorizationRequestRepository = HttpSessionOAuth2AuthorizationRequestRepository.getInstance();

    public OAuth2LoginAuthenticationFilter(
            OAuth2UserService userService,
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        this.authenticationManager = new ProviderManager(
                List.of(new OAuth2LoginAuthenticationProvider(userService))
        );
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        doFilterInternal((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        if (!matchesPattern(request)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Authentication authResult = attemptAuthentication(request);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);

            response.sendRedirect("/"); // 인증 완료 후 리다이렉트
        } catch (AuthenticationException e) {
            logger.info("Authentication failed: {}", e.getMessage());
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        } finally {
            SecurityContext context = SecurityContextHolder.getContext();
            securityContextRepository.saveContext(context, request, response);
            SecurityContextHolder.clearContext();
        }
    }

    private boolean matchesPattern(HttpServletRequest request) {
        return request.getRequestURI().startsWith(LOGIN_CALL_BACK_URI);
    }

    private Authentication attemptAuthentication(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest =
                authorizationRequestRepository.removeAuthorizationRequest(request);
        if (authorizationRequest == null) {
            throw new AuthenticationException("Authorization request not found");
        }

        OAuth2AuthorizationResponse authorizationResponse = convertToResponse(authorizationRequest, request);

        ClientRegistration registration =
                clientRegistrationRepository.findByRegistrationId(authorizationRequest.registrationId());

        Authentication authentication = OAuth2LoginAuthenticationToken.unauthenticated(
                registration,
                new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse)
        );

        return this.authenticationManager.authenticate(authentication);
    }

    private OAuth2AuthorizationResponse convertToResponse(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest httpRequest
    ) {
        String authorizationCode = httpRequest.getParameter(OAuth2ParameterNames.CODE);
        if (!StringUtils.hasText(authorizationCode)) {
            throw new AuthenticationException("Authorization code is missing");
        }

        return new OAuth2AuthorizationResponse(
                authorizationRequest.redirectUri(),
                authorizationCode
        );
    }
}
