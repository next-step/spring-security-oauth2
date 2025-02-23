package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2LoginRedirectFilter extends OncePerRequestFilter {

    private final OAuth2ClientFactory oAuth2ClientFactory;

    public OAuth2LoginRedirectFilter(OAuth2ClientFactory oAuth2ClientFactory) {
        this.oAuth2ClientFactory = oAuth2ClientFactory;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        OAuth2Client oAuth2Client = oAuth2ClientFactory.getOAuth2ClientForLogin(request);
        if (oAuth2Client == null) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.sendRedirect(oAuth2Client.getLoginRedirectUri());
    }
}
