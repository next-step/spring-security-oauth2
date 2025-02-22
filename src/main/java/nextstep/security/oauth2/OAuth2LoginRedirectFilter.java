package nextstep.security.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.PathPatternRequestMatcher;
import nextstep.security.oauth2.registration.ClientRegistration;
import nextstep.security.oauth2.registration.ClientRegistrationRepository;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class OAuth2LoginRedirectFilter extends GenericFilterBean {
    private static final String REGISTRATION = "registration-id";
    private static final String OAUTH_REQUEST_URI_PATTERN = "/oauth2/authorization/{" + REGISTRATION + "}";

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final PathPatternRequestMatcher pathPatternRequestMatcher;

    public OAuth2LoginRedirectFilter(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.pathPatternRequestMatcher = new PathPatternRequestMatcher(HttpMethod.GET, OAUTH_REQUEST_URI_PATTERN);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = ((HttpServletResponse) response);
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);

        if (!pathPatternRequestMatcher.matches(httpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        final String registrationId = pathPatternRequestMatcher.getPathVariable(httpServletRequest, REGISTRATION);
        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        if (clientRegistration == null) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        httpServletResponse.sendRedirect(clientRegistration.getOauth2AuthorizationRedirectURl().toString());
    }
}
