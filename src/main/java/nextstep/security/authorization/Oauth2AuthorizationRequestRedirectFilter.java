package nextstep.security.authorization;

import nextstep.security.access.matcher.RequestMatcher;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Oauth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher;
    private final String redirectUrl;

    public Oauth2AuthorizationRequestRedirectFilter(RequestMatcher requestMatcher, String redirectUrl) {
        this.requestMatcher = requestMatcher;
        this.redirectUrl = redirectUrl;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                response.sendRedirect(redirectUrl);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
