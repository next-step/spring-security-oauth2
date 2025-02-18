package nextstep.security.oauth2.exchange;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.oauth2.client.ClientRegistration;
import nextstep.security.oauth2.client.ClientRegistrationRepository;

public class DefaultOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private static final String OAUTH2_AUTHORIZATION_URL = "/oauth2/authorization/";
    private final ClientRegistrationRepository clientRegistrationRepository;

    public DefaultOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        String registrationId = resolveRegistrationId(request);
        if (registrationId == null) {
            return null;
        }

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            return null;
        }

        return OAuth2AuthorizationRequest.builder()
                                         .registrationId(clientRegistration.getRegistrationId())
                                         .clientId(clientRegistration.getClientId())
                                         .redirectUri(clientRegistration.getRedirectUri())
                                         .scopes(clientRegistration.getScopes())
                                         .authorizationRequestUri(clientRegistration.getAuthorizationRequestUri())
                                         .build();
    }

    private String resolveRegistrationId(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith(OAUTH2_AUTHORIZATION_URL)) {
            return path.substring(OAUTH2_AUTHORIZATION_URL.length());
        }

        return null;
    }

}
