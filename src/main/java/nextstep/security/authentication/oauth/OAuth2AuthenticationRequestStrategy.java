package nextstep.security.authentication.oauth;

public interface OAuth2AuthenticationRequestStrategy {
    String RESPONSE_TYPE = "code";
    String getRegistrationId();
    String getBaseRequestUri();
}
