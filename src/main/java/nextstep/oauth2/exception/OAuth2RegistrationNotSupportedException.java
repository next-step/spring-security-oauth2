package nextstep.oauth2.exception;

public class OAuth2RegistrationNotSupportedException extends RuntimeException {
    private static final String OAUTH_REGISTRATION_NOT_SUPPORT_MSG = "Client Registration not supported for registrationId:";

    public OAuth2RegistrationNotSupportedException(String registrationId) {
        super(OAUTH_REGISTRATION_NOT_SUPPORT_MSG + registrationId);
    }
}
