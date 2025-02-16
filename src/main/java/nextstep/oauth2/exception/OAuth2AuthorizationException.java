package nextstep.oauth2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class OAuth2AuthorizationException extends RuntimeException {
    public OAuth2AuthorizationException(String message) {
        super(message);
    }

    public OAuth2AuthorizationException() {
        super("OAuth2 인가에 실패했습니다.");
    }
}
