package nextstep.security.authentication.filter.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.*;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class OAuth2LoginAuthenticationFilter extends GenericFilterBean {

    private final MvcRequestMatcher[] requestMatchers;
    private final OAuth2TokenRequester auth2TokenRequester;
    private final OAuth2EmailResolver oAuth2EmailResolver;
    private final UserDetailsService userDetailsService;

    public OAuth2LoginAuthenticationFilter(OAuth2TokenRequester auth2TokenRequester, OAuth2EmailResolver oAuth2EmailResolver, UserDetailsService userDetailsService) {
        this.requestMatchers = new MvcRequestMatcher[]{
                new MvcRequestMatcher(HttpMethod.GET, "/login/oauth2/code/github"),
                new MvcRequestMatcher(HttpMethod.GET, "/login/oauth2/code/google")
        };
        this.auth2TokenRequester = auth2TokenRequester;
        this.oAuth2EmailResolver = oAuth2EmailResolver;
        this.userDetailsService = userDetailsService;
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

            String username = oAuth2EmailResolver.resolve(oAuth2Type, tokenResponse);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            OAuth2AuthenticationToken authenticated = OAuth2AuthenticationToken.authenticated(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticated);
            request.getSession()
                    .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            response.sendRedirect("/");
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

    private String getCodeFromParam(HttpServletRequest request) {
        String code = request.getParameter("code");
        if (StringUtils.hasText(code)) {
            return code;
        }

        throw new AuthenticationException("Invalid code");
    }
}
