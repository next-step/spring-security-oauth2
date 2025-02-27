package nextstep.oauth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

public class AuthorizationRequestResolver {
    private static final String MATCH_REQUEST_URI_PREFIX = "/oauth2/authorization/";
    private final ClientRegistrationRepository clientRegistrationRepository;

    public AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        if (!request.getRequestURI().startsWith(MATCH_REQUEST_URI_PREFIX)) {
            return null;
        }
        String providerKey = request.getRequestURI().replaceFirst(MATCH_REQUEST_URI_PREFIX, "");

        ClientRegistration clientRegistration = clientRegistrationRepository.findByProviderKey(providerKey);

        String uriString = UriComponentsBuilder.fromHttpUrl(clientRegistration.getAuthorizationUri())
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("response_type", "code")
                .queryParam("scope", clientRegistration.getScope())
                .queryParam("redirect_uri", clientRegistration.getRedirectUri())
                .toUriString();

        return new OAuth2AuthorizationRequest(uriString);
    }
}
