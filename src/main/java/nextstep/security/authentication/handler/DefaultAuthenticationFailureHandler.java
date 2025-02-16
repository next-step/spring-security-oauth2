package nextstep.security.authentication.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.AuthenticationException;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final HttpStatus status = HttpStatus.UNAUTHORIZED;

    private DefaultAuthenticationFailureHandler() {}

    public static DefaultAuthenticationFailureHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.sendError(status.value(), status.getReasonPhrase());
    }

    private static class SingletonHolder {
        private static final DefaultAuthenticationFailureHandler INSTANCE = new DefaultAuthenticationFailureHandler();
    }
}
