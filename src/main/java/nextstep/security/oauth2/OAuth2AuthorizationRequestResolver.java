package nextstep.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.properties.ClientRegistrationRepository;
import nextstep.security.utils.UrlUtils;
import org.springframework.lang.Nullable;

public class OAuth2AuthorizationRequestResolver {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Nullable
    public OAuth2AuthorizationRequest resolve(final HttpServletRequest request) {
        var registrationId = UrlUtils.getLastPath(request);
        var registration = clientRegistrationRepository.findRegistrationById(registrationId);
        if(registration == null) {
            return null;
        }
        return new OAuth2AuthorizationRequest(registration);
    }

}
