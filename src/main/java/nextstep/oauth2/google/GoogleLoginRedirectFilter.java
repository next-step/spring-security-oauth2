package nextstep.oauth2.google;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class GoogleLoginRedirectFilter extends OncePerRequestFilter {

    public static final String GOOGLE_LOGIN_REQUEST_URI = "/oauth2/authorization/google";
    public static final String GOOGLE_LOGIN_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String REDIRECT_URI = "http://localhost:8080/login/oauth2/code/google";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        if (isNotGoogleAuthorizationURI(request)) {
            doFilter(request, response, filterChain);
            return;
        }

        final String uriString = buildRedirectURI();
        System.out.println("uriString = " + uriString);
        response.sendRedirect(uriString);
    }

    private String buildRedirectURI() {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(GOOGLE_LOGIN_URI);
        uriBuilder.queryParam("response_type", "code");
        uriBuilder.queryParam("client_id", "176481963463-r1n0954hb0g0tmrefbiq920k7014ka0n.apps.googleusercontent.com");
        uriBuilder.queryParam("scope", "email profile");
        uriBuilder.queryParam("redirect_uri", REDIRECT_URI);

        return uriBuilder.toUriString();
    }

    private boolean isNotGoogleAuthorizationURI(final HttpServletRequest request) {
        return !request.getRequestURI().equals(GOOGLE_LOGIN_REQUEST_URI);
    }
}
