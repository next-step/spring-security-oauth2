package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {

    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();

    public OAuth2AuthorizationRequestRedirectFilter(ClientRegistrationRepository clientRegistrationRepository) {
        this.authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestResolver.resolve(request);
        if (authorizationRequest != null) {
            sendRedirectAuthorization(request, response, authorizationRequest);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendRedirectAuthorization(HttpServletRequest request, HttpServletResponse response, OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.sendRedirect(authorizationRequest.getLoginRedirectUri());
    }
}
