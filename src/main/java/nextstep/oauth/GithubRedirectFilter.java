package nextstep.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class GithubRedirectFilter extends GenericFilterBean {
    private static final String MATCH_REQUEST_URI = "/oauth2/authorization/github";
    private static final String GITHUB_AUTHORIZATION_URI = "https://github.com/login/oauth/authorize";
    private static final String GITHUB_CLIENT_ID = "mock_github_client_id";
    private static final String GITHUB_SCOPE = "read:user";
    private static final String GITHUB_APPROVED_REDIRECT_URI = "http://localhost:8080/login/oauth2/code/github";

    private static final String RESPONSE_TYPE = "code";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!request.getRequestURI().equals(MATCH_REQUEST_URI)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String url = UriComponentsBuilder.fromHttpUrl(GITHUB_AUTHORIZATION_URI)
                .queryParam("client_id", GITHUB_CLIENT_ID)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("scope", GITHUB_SCOPE)
                .queryParam("redirect_uri", GITHUB_APPROVED_REDIRECT_URI)
                .toUriString();
        response.sendRedirect(url);
    }
}
