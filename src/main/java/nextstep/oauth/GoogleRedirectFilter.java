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

public class GoogleRedirectFilter extends GenericFilterBean {
    private static final String MATCH_REQUEST_URI = "/oauth2/authorization/google";
    private static final String RESPONSE_TYPE = "code";
    private static final String GOOGLE_AUTHORIZATION_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_CLIENT_ID = "mock_google_client_id";
    private static final String GOOGLE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private static final String GOOGLE_APPROVED_REDIRECT_URI = "http://localhost:8080/login/oauth2/code/google";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!request.getRequestURI().equals(MATCH_REQUEST_URI)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String url = UriComponentsBuilder.fromHttpUrl(GOOGLE_AUTHORIZATION_URI)
                .queryParam("client_id", GOOGLE_CLIENT_ID)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("scope", GOOGLE_SCOPE)
                .queryParam("redirect_uri", GOOGLE_APPROVED_REDIRECT_URI)
                .toUriString();

        response.sendRedirect(url);
    }
}
