package nextstep.security.authentication;

import static nextstep.security.authentication.GithubLoginRedirectFilter.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

public class GithubAuthenticationFilter extends GenericFilterBean {
    private static final String REQUEST_URI = "/login/oauth2/code/github";
    private final RestTemplate restTemplate = new RestTemplate();
    private final GithubGetAccessTokenClient githubGetAccessTokenClient = new GithubGetAccessTokenClient();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String requestURI = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();

        if (requestURI.startsWith(REQUEST_URI) && method.equals(AUTHORIZATION_REQUEST_METHOD.name())) {
            String code = httpServletRequest.getParameter("code");
            String accessToken = githubGetAccessTokenClient.getAccessToken(code);

            return;
        }

        chain.doFilter(request, response);
    }
}
