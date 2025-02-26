package nextstep.security.authentication;

public class AccessTokenResponseDTO {
    private String access_token;
    private String scope;
    private String token_type;

    public String getAccess_token() {
        return access_token;
    }

    public String getScope() {
        return scope;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getToken() {
        return token_type + " " + access_token;
    }
}
