package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.http.HttpMethod;

import java.util.List;

public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final MvcRequestMatcher DEFAULT_MVC_REQUEST_MATCHER = new MvcRequestMatcher(HttpMethod.POST, "/login");
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

    private final AuthenticationManager authenticationManager;

    public UsernamePasswordAuthenticationFilter(UserDetailsService userDetailsService,
                                                AuthenticationSuccessHandler successHandler) {
        super(DEFAULT_MVC_REQUEST_MATCHER, successHandler, DefaultAuthenticationFailHandler.getInstance());
        this.authenticationManager = new ProviderManager(
                List.of(new DaoAuthenticationProvider(userDetailsService))
        );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String username = obtainUsername(request);
        username = (username != null) ? username.trim() : "";
        String password = obtainPassword(request);
        password = (password != null) ? password : "";

        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        return this.authenticationManager.authenticate(unauthenticated);
    }

    private String obtainUsername(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
    }

    private String obtainPassword(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
    }
}
