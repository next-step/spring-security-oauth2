package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.RegexRequestMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2LoginRedirectFilter extends OncePerRequestFilter {

    private static final RegexRequestMatcher OAUTH2_LOGIN_REQUEST_MATCHER = new RegexRequestMatcher(HttpMethod.GET, "/oauth2/authorization/.*");
    private final OAuth2ClientRepository oAuth2ClientRepository;

    public OAuth2LoginRedirectFilter(OAuth2ClientRepository oAuth2ClientRepository) {
        this.oAuth2ClientRepository = oAuth2ClientRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!OAUTH2_LOGIN_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientRegistrationId = extractRegistrationId(request);
        ClientRegistration clientRegistration = oAuth2ClientRepository.findByRegistrationId(clientRegistrationId);
        if (clientRegistration == null) {
            throw new AuthenticationException();
        }

        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.sendRedirect(clientRegistration.getLoginRedirectUri());
    }

    private static String extractRegistrationId(HttpServletRequest request) {
        return request.getRequestURI().substring("/oauth2/authorization/".length());
    }
}
