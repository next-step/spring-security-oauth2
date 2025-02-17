package nextstep.security.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class GithubLoginRedirectFilter extends GenericFilterBean {
    private final static String GIT_HUB_OAUTH_REQUEST_URI = "/oauth2/authorization/github";
    private final static String GITHUB_LOGIN_URL = "https://github.com/login/oauth/authorize";

    private final GithubOauth2LoginRequest githubOauth2LoginRequest;

    public GithubLoginRedirectFilter(GithubOauth2LoginRequest githubOauth2LoginRequest) {
        this.githubOauth2LoginRequest = githubOauth2LoginRequest;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest =  ((HttpServletRequest) request);
        HttpServletResponse httpServletResponse = ((HttpServletResponse) response);
        String requestURI = httpServletRequest.getRequestURI();

        if (requestURI.matches(GIT_HUB_OAUTH_REQUEST_URI)) {
            httpServletResponse.sendRedirect(UriComponentsBuilder.fromHttpUrl(GITHUB_LOGIN_URL)
                    .queryParam("client_id", githubOauth2LoginRequest.clientId())
                    .queryParam("response_type", githubOauth2LoginRequest.responseType())
                    .queryParam("redirect_uri", githubOauth2LoginRequest.redirectUri())
                    .queryParam("scope", githubOauth2LoginRequest.scope())
                    .build()
                    .toUriString());
            return;
        }

        chain.doFilter(request, httpServletResponse);
    }

}
