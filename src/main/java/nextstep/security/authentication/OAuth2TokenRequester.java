package nextstep.security.authentication;

public interface OAuth2TokenRequester {
    TokenResponse request(String oAuth2Type, String code);
}
