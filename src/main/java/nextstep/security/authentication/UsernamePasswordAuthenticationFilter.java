package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import org.springframework.http.HttpMethod;

public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
    private static final MvcRequestMatcher DEFAULT_MVC_REQUEST_MATCHER = new MvcRequestMatcher(HttpMethod.POST, "/login");
    private static final HttpSessionSecurityContextRepository securityContextRepository = HttpSessionSecurityContextRepository.getInstance();

    public UsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_MVC_REQUEST_MATCHER, authenticationManager);
    }

    @Override
    protected Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        return getAuthenticationManager().authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        obtainUsername(request), obtainPassword(request)
                )
        );
    }

    private String obtainUsername(HttpServletRequest request) {
        return trim(request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY));
    }

    private String obtainPassword(HttpServletRequest request) {
        return trim(request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY));
    }

    private String trim(String str) {
        return str == null || str.isBlank()
                ? ""
                : str.trim();
    }
}
