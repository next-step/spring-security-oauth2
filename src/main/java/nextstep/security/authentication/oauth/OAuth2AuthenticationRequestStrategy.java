package nextstep.security.authentication.oauth;

public interface OAuth2AuthenticationRequestStrategy {
    String RESPONSE_TYPE = "code";
    String getOAuth2Type();
    String getScope();
    String getClientId();
    String getClientSecret();
    String getBaseRequestUri();
    String getRedirectUri();
}
