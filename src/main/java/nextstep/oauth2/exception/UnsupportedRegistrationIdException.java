package nextstep.oauth2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnsupportedRegistrationIdException extends RuntimeException {
    public UnsupportedRegistrationIdException() {
        super("Unsupported registration ID");
    }
}
