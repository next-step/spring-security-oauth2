package nextstep.app.config;

import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Oauth2AuthorizationFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String paramsQuery = UriComponentsBuilder.newInstance()
                    .queryParam("client_id", "04f8811a8c4aafe9baab")
                    .queryParam("response_type", "code")
                    .queryParam("scope", "read:user")
                    .queryParam("redirect_uri", "http://localhost:8080/login/oauth2/code/github")
                    .build()
                    .toUri()
                    .getQuery();
            response.sendRedirect("https://github.com/login/oauth/authorize?" + paramsQuery);
        } catch (Exception e) {
            throw e;
        }

        chain.doFilter(request, response);
    }
}
