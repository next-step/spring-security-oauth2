package nextstep.security.oauth2.endpoint;

import nextstep.security.oauth2.client.ClientRegistration;
import nextstep.security.oauth2.exception.OAuth2RemoteClientNotFoundException;
import nextstep.security.oauth2.user.OAuth2User;

import java.util.List;

public class OAuth2RemoteClientAdapter {

    private final List<OAuth2RemoteClient> oAuth2RemoteClients;

    public OAuth2RemoteClientAdapter(List<OAuth2RemoteClient> oAuth2RemoteClients) {
        this.oAuth2RemoteClients = oAuth2RemoteClients;
    }

    public OAuth2AccessToken getAccessToken(ClientRegistration clientRegistration, String code) {

        OAuth2RemoteClient oAuth2RemoteClient = resolveOAuthRemoteClient(clientRegistration.getRegistrationId());
        return oAuth2RemoteClient.getAccessToken(clientRegistration.getClientId(),
                                                 clientRegistration.getClientSecret(),
                                                 code,
                                                 clientRegistration.getRedirectUri(),
                                                 "authorization_code");
    }

    public OAuth2User getUserInfo(String registrationId, OAuth2AccessToken accessToken) {
        OAuth2RemoteClient oAuth2RemoteClient = resolveOAuthRemoteClient(registrationId);
        return oAuth2RemoteClient.getUserInfo(accessToken);
    }

    private OAuth2RemoteClient resolveOAuthRemoteClient(String registrationId) {
        for (OAuth2RemoteClient oAuth2RemoteClient : oAuth2RemoteClients) {
            if (oAuth2RemoteClient.supports(registrationId)) {
                return oAuth2RemoteClient;
            }
        }

        throw new OAuth2RemoteClientNotFoundException("Cannot found remote client registrationId: " + registrationId);
    }
}
