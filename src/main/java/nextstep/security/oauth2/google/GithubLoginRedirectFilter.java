package nextstep.security.oauth2.google;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class GithubLoginRedirectFilter extends OncePerRequestFilter {

    public static final String GITHUB_LOGIN_REQUEST_URI = "/oauth2/authorization/github";
    public static final String GITHUB_LOGIN_URI = "https://github.com/login/oauth/authorize";
    public static final String REDIRECT_URI = "http://localhost:8080/login/oauth2/code/github";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        if (isNotGitHubAuthorizationURI(request)) {
            doFilter(request, response, filterChain);
            return;
        }

        final String uriString = buildRedirectURI();
        response.sendRedirect(uriString);
    }

    private String buildRedirectURI() {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(GITHUB_LOGIN_URI);
        uriBuilder.queryParam("response_type", "code");
        uriBuilder.queryParam("client_id", "Ov23liWTGdV9MeJBRLwC");
        uriBuilder.queryParam("scope", "read:user");
        uriBuilder.queryParam("redirect_uri", REDIRECT_URI);

        return uriBuilder.toUriString();
    }

    private boolean isNotGitHubAuthorizationURI(final HttpServletRequest request) {
        return !request.getRequestURI().equals(GITHUB_LOGIN_REQUEST_URI);
    }
}
