package nextstep.oauth2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnsupportedRegistrationIdException extends RuntimeException {
    public UnsupportedRegistrationIdException(String registrationId) {
        super("지원하지 않는 registration ID 입니다; " + registrationId);
    }
}
