package nextstep.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.PathPatternRequestMatcher;
import nextstep.security.oauth2.registration.ClientRegistration;
import nextstep.security.oauth2.registration.ClientRegistrationRepository;
import org.springframework.http.HttpMethod;

public class OAuth2AuthorizationRequestResolver {
    private static final String REGISTRATION = "registration-id";
    private static final String OAUTH_REQUEST_URI_PATTERN = "/oauth2/authorization/{" + REGISTRATION + "}";
    private static final PathPatternRequestMatcher matcher = new PathPatternRequestMatcher(HttpMethod.GET, OAUTH_REQUEST_URI_PATTERN);

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        String registrationId = matcher.getPathVariable(request, REGISTRATION);

        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        if (clientRegistration == null) {
            return null;
        }

        return OAuth2AuthorizationRequest.from(clientRegistration);
    }
}
