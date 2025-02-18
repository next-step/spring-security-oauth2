package nextstep.security.authentication;

public interface OAuth2EmailResolver {
    String resolve(String oAuth2Type, TokenResponse token);
}
