package nextstep.security.userservice;


public interface OAuth2ClientRegistration {
    String getRegistrationId();
    String getClientId();
    String getRedirectUri();
    String getScope();
}
