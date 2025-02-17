package nextstep.security.authentication;

@SuppressWarnings("unused")
public interface TokenResponse {
    String getAccessToken();
    String getTokenType();
}
