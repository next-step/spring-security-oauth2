package nextstep.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class OAuth2LoginAuthenticationFilter extends GenericFilterBean {
    private static final String MATCH_REQUEST_URI_PREFIX = "/login/oauth2/code/";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (!requiresAuthentication(request, response)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Authentication authenticationResult = attemptAuthentication(request, response);
            if (authenticationResult == null) {
                return;
            }
//            successfulAuthentication(request, response, filterChain, authenticationResult);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
        }
    }

    private boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return request.getRequestURI().startsWith(MATCH_REQUEST_URI_PREFIX);
    }

    private Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        // request에서 parameter를 가져오기
        String code = request.getParameter("code");
        // session에서 authorizationRequest를 가져오기


        // registrationId를 가져오고 clientRegistration을 가져오기

        // code를 포함한 authorization response를 객체로 가져오기

        // access token 을 가져오기 위한 request 객체 만들기

        // OAuth2LoginAuthenticationToken 만들기

        // provider 인증 후 authenticated된 OAuth2AuthenticationToken 객체 가져오기

        // authorizedClientRepository 에 저장할 OAuth2AuthorizedClient을 만들고 저장

        return null;
    }
}
