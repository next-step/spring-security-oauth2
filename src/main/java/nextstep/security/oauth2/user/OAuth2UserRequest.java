package nextstep.security.oauth2.user;

import nextstep.security.oauth2.OAuth2AccessToken;
import nextstep.security.oauth2.registration.ClientRegistration;

public class OAuth2UserRequest {
    private final ClientRegistration clientRegistration;
    private final OAuth2AccessToken accessToken;

    public OAuth2UserRequest(ClientRegistration clientRegistration, OAuth2AccessToken accessToken) {
        this.clientRegistration = clientRegistration;
        this.accessToken = accessToken;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }
}
