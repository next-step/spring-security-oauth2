package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

public class GithubLoginRedirectFilter extends GenericFilterBean {
    private static final String REQUEST_URI = "/oauth2/authorization/github";
    public static final HttpMethod AUTHORIZATION_REQUEST_METHOD = HttpMethod.GET;
    public static final String GITHUB_AUTHORIZATION_URI = "https://github.com/login/oauth/authorize?";
    public static final String REDIRECT_URI = "http://localhost:8080/login/oauth2/code/github";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String requestURI = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();

        if (requestURI.equals(REQUEST_URI) && method.equals(AUTHORIZATION_REQUEST_METHOD.name())) {
            String queryString = UriComponentsBuilder.newInstance()
                    .queryParam("client_id", "Ov23lijfjg9lkGyYVDXN")
                    .queryParam("scope", "read:user")
                    .queryParam("response_type", "code")
                    .queryParam("redirect_uri", REDIRECT_URI)
                    .build()
                    .toUri()
                    .getQuery();

            httpServletResponse.sendRedirect(GITHUB_AUTHORIZATION_URI + queryString);
            return;
        }

        chain.doFilter(request, response);
    }
}
