package nextstep.security.userservice;


public interface OAuth2ClientRegistration {
    String getRegistrationId();
    String getClientId();
    String getClientSecret();
    String getRedirectUri();
    String getScope();
}
