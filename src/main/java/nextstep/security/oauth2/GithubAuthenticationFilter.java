package nextstep.security.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GithubAuthenticationFilter extends GenericFilterBean {
    private static final String GIT_HUB_OAUTH_LOGIN_REQUEST_URI = "/login/oauth2/code/github";
    public static final String ACCESS_TOKE_URI = "http://localhost:8089/login/oauth/access_token";
    public static final String RESOURCE_USER_URI= "http://localhost:8089/user";
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final UserDetailsService userDetailsService;

    public GithubAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest =  ((HttpServletRequest) request);
        HttpServletResponse httpServletResponse = ((HttpServletResponse) response);

        String requestURI = httpServletRequest.getRequestURI();

        if (requestURI.matches(GIT_HUB_OAUTH_LOGIN_REQUEST_URI)) {
            Map<String, String> body = RestClient.create().post()
                    .uri(ACCESS_TOKE_URI)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            String accessToken = body.get("access_token");
            String tokenType = body.get("token_type");


            Map<String, String> user = RestClient.create()
                    .get()
                    .uri(RESOURCE_USER_URI)
                    .header(HttpHeaders.AUTHORIZATION, tokenType + accessToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });


            String email = user.get("email");

            Optional<UserDetails> userDetails = userDetailsService.loadUser(email);

            if (userDetails.isEmpty()) {
                userDetailsService.saveUser(email);
            }

            Oauth2AuthenticationToken authenticated = Oauth2AuthenticationToken.authenticated(user.get("email"), accessToken, Set.of());
            SecurityContext securityContext = new SecurityContext();
            securityContext.setAuthentication(authenticated);

            securityContextRepository.saveContext(securityContext, httpServletRequest, httpServletResponse);
            httpServletResponse.sendRedirect("/");
            return;
        }

        chain.doFilter(request, httpServletResponse);
    }

}
