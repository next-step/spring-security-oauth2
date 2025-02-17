package nextstep.security.authentication.filter.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.OAuth2AuthenticationRequestResolver;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public abstract class OAuth2RedirectAuthenticationFilter extends GenericFilterBean {

    private final MvcRequestMatcher requestMatcher;
    private final OAuth2AuthenticationRequestResolver oAuth2AuthenticationRequestResolver;

    protected OAuth2RedirectAuthenticationFilter(MvcRequestMatcher requestMatcher,OAuth2AuthenticationRequestResolver oAuth2AuthenticationRequestResolver) {
        this.requestMatcher = requestMatcher;
        this.oAuth2AuthenticationRequestResolver = oAuth2AuthenticationRequestResolver;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest request
                && servletResponse instanceof HttpServletResponse response) {

            if (noRequiresAuthentication(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String authorizationUri = oAuth2AuthenticationRequestResolver.resolve();
            response.sendRedirect(authorizationUri);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean noRequiresAuthentication(HttpServletRequest request) {
        return !requestMatcher.matches(request);
    }
}
