package nextstep.security.authentication;

import nextstep.security.access.matcher.RequestMatcher;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Oauth2LoginAuthenticationFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final String redirectUrl;

    public Oauth2LoginAuthenticationFilter(
        RequestMatcher requestMatcher,
        AuthenticationManager authenticationManager,
        SecurityContextRepository securityContextRepository,
        String redirectUrl
    ) {
        this.requestMatcher = requestMatcher;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.redirectUrl = redirectUrl;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            final String accessToken = request.getParameter("code");

            final SecurityContext context = SecurityContextHolder.getContext();

            final Authentication authRequest = Oauth2Authentication.ofRequest(accessToken);
            final Authentication authResult = authenticationManager.authenticate(authRequest);

            context.setAuthentication(authResult);
            securityContextRepository.saveContext(context, request, response);
            response.sendRedirect(redirectUrl);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
