package nextstep.oauth;

public class OAuth2AuthorizationRequest {
    private String authorizationRequestUrl;

    public OAuth2AuthorizationRequest(String authorizationRequestUrl) {
        this.authorizationRequestUrl = authorizationRequestUrl;
    }

    public String getAuthorizationRequestUrl() {
        return authorizationRequestUrl;
    }
}
