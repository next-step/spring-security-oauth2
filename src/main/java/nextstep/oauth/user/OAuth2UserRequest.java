package nextstep.oauth.user;

import nextstep.oauth.ClientRegistration;

public class OAuth2UserRequest {
    private final ClientRegistration clientRegistration;
    private final String accessToken;

    public OAuth2UserRequest(ClientRegistration clientRegistration, String accessToken) {
        this.clientRegistration = clientRegistration;
        this.accessToken = accessToken;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
