package nextstep.security.authentication;

public class OAuth2AuthorizedClientId {
    private String clientId;
    private String name;

    public OAuth2AuthorizedClientId(String clientId, String name) {
        this.clientId = clientId;
        this.name = name;
    }
}
