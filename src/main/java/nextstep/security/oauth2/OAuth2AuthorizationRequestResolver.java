package nextstep.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.PathPatternRequestMatcher;
import nextstep.security.oauth2.registration.ClientRegistration;
import nextstep.security.oauth2.registration.ClientRegistrationRepository;
import org.springframework.http.HttpMethod;

public class OAuth2AuthorizationRequestResolver {
    private static final String REGISTRATION = "registration-id";
    private static final String OAUTH_REQUEST_URI_PATTERN = "/{" + REGISTRATION + "}";
    private final PathPatternRequestMatcher matcher;

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2AuthorizationRequestResolver(String baseUrl, ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.matcher = new PathPatternRequestMatcher(HttpMethod.GET, baseUrl + OAUTH_REQUEST_URI_PATTERN);
    }

    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        String registrationId = matcher.getPathVariable(request, REGISTRATION);

        if (registrationId == null) {
            return null;
        }

        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        if (clientRegistration == null) {
            return null;
        }

        return OAuth2AuthorizationRequest.from(clientRegistration);
    }
}
