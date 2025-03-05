package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public abstract class AbstractAuthenticationProcessingFilter extends GenericFilterBean {

    private final RequestMatcher requestMatcher;

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailHandler authenticationFailHandler;

    public AbstractAuthenticationProcessingFilter(final RequestMatcher requestMatcher,
                                                  final AuthenticationSuccessHandler authenticationSuccessHandler,
                                                  final AuthenticationFailHandler authenticationFailHandler) {
        this.requestMatcher = requestMatcher;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailHandler = authenticationFailHandler;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!requiresAuthentication(request)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            var authenticationResult = attemptAuthentication(request, response);
            if (authenticationResult == null) {
                return;
            }
            authenticationSuccessHandler.onSuccess(request, response, authenticationResult);
        } catch (AuthenticationException ex) {
            authenticationFailHandler.onFail(request, response, ex);
        }
    }

    protected abstract Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response);

    private boolean requiresAuthentication(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

}
