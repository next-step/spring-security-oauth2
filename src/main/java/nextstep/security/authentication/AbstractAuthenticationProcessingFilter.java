package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.RequestMatcher;
import nextstep.security.authentication.handler.AuthenticationFailureHandler;
import nextstep.security.authentication.handler.AuthenticationSuccessHandler;
import nextstep.security.authentication.handler.DefaultAuthenticationFailureHandler;
import nextstep.security.authentication.handler.DefaultAuthenticationSuccessHandler;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.context.SecurityContextRepository;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public abstract class AbstractAuthenticationProcessingFilter extends GenericFilterBean {
    private static final SecurityContextRepository securityContextRepository = HttpSessionSecurityContextRepository.getInstance();
    private static final AuthenticationSuccessHandler successHandler = DefaultAuthenticationSuccessHandler.getInstance();
    private static final AuthenticationFailureHandler failureHandler = DefaultAuthenticationFailureHandler.getInstance();

    private final RequestMatcher requiresAuthenticationRequestMatcher;
    private final AuthenticationManager authenticationManager;

    protected AbstractAuthenticationProcessingFilter(RequestMatcher requiresAuthenticationRequestMatcher, AuthenticationManager authenticationManager) {
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
        this.authenticationManager = authenticationManager;
    }

    protected AbstractAuthenticationProcessingFilter(String filterProcessesUrl, AuthenticationManager authenticationManager) {
        this(request -> request.getRequestURI().startsWith(filterProcessesUrl), authenticationManager);
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    protected abstract Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException, IOException, ServletException;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        if (!requiresAuthenticationRequestMatcher.matches(request)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            final Authentication authentication = attemptAuthentication(request, response);
            if (authentication == null) {
                return;
            }
            final SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            successHandler.onAuthenticationSuccess(request, response, authentication);
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            failureHandler.onAuthenticationFailure(request, response, e);
        }
    }
}
