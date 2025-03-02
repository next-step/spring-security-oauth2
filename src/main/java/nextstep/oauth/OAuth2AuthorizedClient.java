package nextstep.oauth;

public class OAuth2AuthorizedClient {
    private final ClientRegistration clientRegistration;

    private final String principalName;

    private final String accessToken;

    public OAuth2AuthorizedClient(ClientRegistration clientRegistration, String principalName, String accessToken) {
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

    public String getAccessToken() {
        return accessToken;
    }
}
