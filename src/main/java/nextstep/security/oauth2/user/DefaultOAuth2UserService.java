package nextstep.security.oauth2.user;

import nextstep.security.oauth2.endpoint.OAuth2AccessToken;
import nextstep.security.oauth2.endpoint.OAuth2RemoteClientAdapter;

public class DefaultOAuth2UserService implements OAuth2UserService {
    private final OAuth2RemoteClientAdapter remoteClientAdapter;

    public DefaultOAuth2UserService(OAuth2RemoteClientAdapter remoteClientAdapter) {
        this.remoteClientAdapter = remoteClientAdapter;
    }

    @Override
    public OAuth2User loadUser(String registrationId, OAuth2AccessToken accessToken) {
        return remoteClientAdapter.getUserInfo(registrationId, accessToken);
    }
}
