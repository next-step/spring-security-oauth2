package nextstep.security.oauth2.exception;

public class ClientRegistrationNotFoundException extends OAuth2Exception {
    public ClientRegistrationNotFoundException(String message) {
        super(message);
    }
}
