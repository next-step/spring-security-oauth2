package nextstep.security.oauth2;

import nextstep.security.oauth2.registration.ClientRegistration;

public class OAuth2AuthorizedClient {

    private final ClientRegistration clientRegistration;

    private final String principalName;

    private final OAuth2AccessToken accessToken;


    public OAuth2AuthorizedClient(ClientRegistration clientRegistration, String principalName, OAuth2AccessToken accessToken) {
        this.clientRegistration = clientRegistration;
        this.principalName = principalName;
        this.accessToken = accessToken;
    }


    public ClientRegistration getClientRegistration() {
        return this.clientRegistration;
    }

    public String getPrincipalName() {
        return this.principalName;
    }

    public OAuth2AccessToken getAccessToken() {
        return this.accessToken;
    }
}
