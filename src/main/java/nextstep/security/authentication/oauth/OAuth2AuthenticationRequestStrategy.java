package nextstep.security.authentication.oauth;

public interface OAuth2AuthenticationRequestStrategy {
    String RESPONSE_TYPE = "code";
    String getRegistrationId();
    String getScope();
    String getClientId();
    String getClientSecret();
    String getBaseRequestUri();
    String getRedirectUri();
}
