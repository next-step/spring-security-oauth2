package nextstep.oauth2.exception;

public class OAuth2RegistrationNotFoundException extends RuntimeException {
    private static final String OAUTH_REGISTRATION_NOT_FOUND_MSG = "Client Registration not found for registrationId:";

    public OAuth2RegistrationNotFoundException(String registrationId) {
        super(OAUTH_REGISTRATION_NOT_FOUND_MSG + registrationId);
    }
}
