package nextstep.security.oauth2.authorizedclient;

import nextstep.security.oauth2.client.ClientRegistration;
import nextstep.security.oauth2.endpoint.OAuth2AccessToken;

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
        return clientRegistration;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }
}
