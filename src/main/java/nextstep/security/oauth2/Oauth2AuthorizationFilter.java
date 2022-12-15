package nextstep.security.oauth2;

import nextstep.security.access.matcher.MvcRequestMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Oauth2AuthorizationFilter extends OncePerRequestFilter {

    private static final MvcRequestMatcher DEFAULT_REQUEST_MATCHER = new MvcRequestMatcher(HttpMethod.GET,
            "/oauth2/authorization/github");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!DEFAULT_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String paramsQuery = UriComponentsBuilder.newInstance()
                .queryParam("client_id", "04f8811a8c4aafe9baab")
                .queryParam("response_type", "code")
                .queryParam("scope", "read:user")
                .queryParam("redirect_uri", "http://localhost:8080/login/oauth2/code/github")
                .build()
                .toUri()
                .getQuery();
        response.sendRedirect("https://github.com/login/oauth/authorize?" + paramsQuery);
    }
}
