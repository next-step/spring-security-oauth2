package nextstep.oauth2.authentication;

public class OAuth2AccessToken {
    private final String value;

    public OAuth2AccessToken(String tokenValue) {
        this.value = tokenValue;
    }

    public String value() {
        return value;
    }
}
