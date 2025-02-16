package nextstep.oauth2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnmatchedStateException extends RuntimeException {
    public UnmatchedStateException() {
        super("HttpRequest 와 세션의 OAuth2 state 가 불일치 합니다.");
    }
}
