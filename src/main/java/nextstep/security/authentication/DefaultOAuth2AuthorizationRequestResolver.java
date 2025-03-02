package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RegexRequestMatcher;
import nextstep.security.access.RequestMatcher;
import org.springframework.http.HttpMethod;

public class DefaultOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final String OAUTH2_AUTHORIZATION_REQUEST_URI = "/oauth2/authorization/";
    private final RequestMatcher requestMatcher = new RegexRequestMatcher(HttpMethod.GET, OAUTH2_AUTHORIZATION_REQUEST_URI + ".*");
    private final ClientRegistrationRepository clientRegistrationRepository;

    public DefaultOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {

        if (!requestMatcher.matches(request)) {
            return null;
        }

        String registrationId = extractRegistrationId(request);
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new InvalidClientRegistrationIdException();
        }

        return OAuth2AuthorizationRequest.from(clientRegistration);
    }

    private String extractRegistrationId(HttpServletRequest request) {
        return request.getRequestURI().substring(OAUTH2_AUTHORIZATION_REQUEST_URI.length());
    }
}

