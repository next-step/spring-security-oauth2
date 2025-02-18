package nextstep.security.authentication;

public interface OAuth2AuthenticationRequestStrategy {
    String RESPONSE_TYPE = "code";
    String getOAuth2Type();
    String getScope();
    String getClientId();
    String getBaseRequestUri();
    String getRedirectUri();
}
