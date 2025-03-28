package nextstep.security.oauth2.client.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.oauth2.client.registration.ClientRegistrationRepository;
import nextstep.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends GenericFilterBean {

    public static final String OAUTH2_LOGIN_REQUEST_URI_PREFIX = "/oauth2/authorization/";
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final AuthorizationRequestRepository authorizationRequestRepository = HttpSessionOAuth2AuthorizationRequestRepository.getInstance();

    public OAuth2AuthorizationRequestRedirectFilter(ClientRegistrationRepository clientRegistrationRepository) {
        this.authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
                OAUTH2_LOGIN_REQUEST_URI_PREFIX,
                clientRegistrationRepository
        );
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
    ) throws ServletException, IOException {
        if (!matchesPattern(request)) {
            chain.doFilter(request, response);
            return;
        }

        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestResolver.resolve(request);
        sendRedirectForAuthorization(authorizationRequest, request, response);
    }

    private void sendRedirectForAuthorization(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        response.sendRedirect(authorizationRequest.redirectUri());
    }

    private boolean matchesPattern(HttpServletRequest request) {
        return request.getRequestURI().startsWith(OAUTH2_LOGIN_REQUEST_URI_PREFIX);
    }
}
