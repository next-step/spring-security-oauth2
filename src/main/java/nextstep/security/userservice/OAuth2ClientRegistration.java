package nextstep.security.userservice;


import java.util.Set;

public interface OAuth2ClientRegistration {
    String getRegistrationId();
    String getClientId();
    String getRedirectUri();
    Set<String> getScope();
}
