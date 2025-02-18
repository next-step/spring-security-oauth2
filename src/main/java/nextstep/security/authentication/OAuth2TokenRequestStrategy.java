package nextstep.security.authentication;

public interface OAuth2TokenRequestStrategy {
    String getOAuth2Type();
    TokenRequest requestToken(String code);
    String getRequestUri();
    Class<? extends TokenResponse> getResponseClass();
}
