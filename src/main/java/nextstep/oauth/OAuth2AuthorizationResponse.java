package nextstep.oauth;

public class OAuth2AuthorizationResponse {
    private String code;
    private String redirectUri;

    public OAuth2AuthorizationResponse(String code, String redirectUri) {
        this.code = code;
        this.redirectUri = redirectUri;
    }

    public String getCode() {
        return code;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
