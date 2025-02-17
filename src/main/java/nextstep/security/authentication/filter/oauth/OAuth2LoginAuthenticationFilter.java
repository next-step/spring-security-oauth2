package nextstep.security.authentication.filter.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.application.OAuth2TokenRequester;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.TokenResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public abstract class OAuth2LoginAuthenticationFilter extends GenericFilterBean {

    private final MvcRequestMatcher requestMatcher;
    private final OAuth2TokenRequester auth2TokenRequester;

    protected OAuth2LoginAuthenticationFilter(MvcRequestMatcher requestMatcher, OAuth2TokenRequester auth2TokenRequester) {
        this.requestMatcher = requestMatcher;
        this.auth2TokenRequester = auth2TokenRequester;
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
            String code = getCodeFromParam(request);
            TokenResponse tokenResponse = auth2TokenRequester.request(oAuth2Type, code);
            if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
                throw new AuthenticationException();
            }

            response.sendRedirect("/");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean noRequiresAuthentication(HttpServletRequest request) {
        return !requestMatcher.matches(request);
    }

    private String getOAuth2Type(String requestUri) {
        return requestUri.substring(requestUri.lastIndexOf("/") + 1);
    }

    private String getCodeFromParam(HttpServletRequest request) {
        String code = request.getParameter("code");
        if (StringUtils.hasText(code)) {
            return code;
        }

        throw new AuthenticationException("Invalid code");
    }
}
