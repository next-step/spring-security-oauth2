package nextstep.security.authentication;

public interface OAuth2EmailResolveStrategy {
    String getOAuth2Type();
    Class<? extends UserResponse> getUserResponseClass();
    String getRequestUri();
}
