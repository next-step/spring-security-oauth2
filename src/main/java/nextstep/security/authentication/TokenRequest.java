package nextstep.security.authentication;

@SuppressWarnings("unused")
public interface TokenRequest {
    String getCode();
    String getClientId();
    String getClientSecret();
    String getRedirectUri();
}
