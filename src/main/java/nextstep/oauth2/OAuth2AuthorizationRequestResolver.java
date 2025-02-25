package nextstep.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.oauth2.registration.ClientRegistrationRepository;

public class OAuth2AuthorizationRequestResolver {
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2AuthorizationRequestResolver(final ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {

    }
}
