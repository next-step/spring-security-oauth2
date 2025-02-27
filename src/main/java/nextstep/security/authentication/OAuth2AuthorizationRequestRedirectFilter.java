package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.RegexRequestMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {

    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;

    public OAuth2AuthorizationRequestRedirectFilter(OAuth2ClientRepository oAuth2ClientRepository) {
        this.authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
                new RegexRequestMatcher(HttpMethod.GET, "/oauth2/authorization/.*"), oAuth2ClientRepository);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ClientRegistration clientRegistration = authorizationRequestResolver.resolve(request);
        if (clientRegistration != null) {
            sendRedirectAuthorization(request, response, clientRegistration);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static void sendRedirectAuthorization(HttpServletRequest request, HttpServletResponse response, ClientRegistration clientRegistration) throws IOException {
        request.getSession().setAttribute("clientRegistration", clientRegistration);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.sendRedirect(clientRegistration.getLoginRedirectUri());
    }
}
