package nextstep.security.authentication;

public class OAuth2AuthorizedClient {
    private final ClientRegistration clientRegistration;
    private final String principalName;
    private final OAuth2AccessToken oAuth2AccessToken;

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public OAuth2AuthorizedClient(ClientRegistration clientRegistration, String principalName, OAuth2AccessToken oAuth2AccessToken) {
        this.clientRegistration = clientRegistration;
        this.principalName = principalName;
        this.oAuth2AccessToken = oAuth2AccessToken;
    }
}
