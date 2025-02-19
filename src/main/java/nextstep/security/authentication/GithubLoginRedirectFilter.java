package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class GithubLoginRedirectFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equals("GET") && request.getRequestURI().equals("/oauth2/authorization/github")) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.sendRedirect("https://github.com/login/oauth/authorize?response_type=code&client_id=Ov23limHOsuGxvCvCFss&scope=read:user&redirect_uri=http://localhost:8080/login/oauth2/code/github");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
