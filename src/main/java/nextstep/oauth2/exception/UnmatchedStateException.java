package nextstep.oauth2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnmatchedStateException extends RuntimeException {
    public UnmatchedStateException() {
        super("OAuth2 state of httpRequest and session are mismatched");
    }
}
