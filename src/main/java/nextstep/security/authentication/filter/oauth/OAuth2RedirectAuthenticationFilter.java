package nextstep.security.authentication.filter.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.OAuth2AuthenticationRequestResolver;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class OAuth2RedirectAuthenticationFilter extends GenericFilterBean {

    private final MvcRequestMatcher[] requestMatchers;
    private final OAuth2AuthenticationRequestResolver oAuth2AuthenticationRequestResolver;

    public OAuth2RedirectAuthenticationFilter(OAuth2AuthenticationRequestResolver oAuth2AuthenticationRequestResolver) {
        this.requestMatchers = new MvcRequestMatcher[]{
                new MvcRequestMatcher(HttpMethod.GET, "/oauth2/authorization/google"),
                new MvcRequestMatcher(HttpMethod.GET, "/oauth2/authorization/github")
        };
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

            String oAuth2Type = getOAuth2Type(request.getRequestURI());

            String authorizationUri = oAuth2AuthenticationRequestResolver.resolve(oAuth2Type);
            response.sendRedirect(authorizationUri);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean noRequiresAuthentication(HttpServletRequest request) {
        for (MvcRequestMatcher requestMatcher : requestMatchers) {
            if (requestMatcher.matches(request)) {
                return false;
            }
        }
        return true;
    }

    private String getOAuth2Type(String requestUri) {
        return requestUri.substring(requestUri.lastIndexOf("/") + 1);
    }
}
