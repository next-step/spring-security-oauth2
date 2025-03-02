package nextstep.security.authentication;

public class OAuth2UserRequest {

    private final OAuth2AccessToken accessToken;
    private final ClientRegistration clientRegistration;

    public OAuth2UserRequest(OAuth2AccessToken accessToken, ClientRegistration clientRegistration) {
        this.accessToken = accessToken;
        this.clientRegistration = clientRegistration;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }
}
