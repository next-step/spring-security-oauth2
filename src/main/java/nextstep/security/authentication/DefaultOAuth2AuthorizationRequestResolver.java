package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;

public class DefaultOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final RequestMatcher requestMatcher;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public DefaultOAuth2AuthorizationRequestResolver(RequestMatcher requestMatcher, ClientRegistrationRepository clientRegistrationRepository) {
        this.requestMatcher = requestMatcher;
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
        return request.getRequestURI().substring("/oauth2/authorization/".length());
    }
}

