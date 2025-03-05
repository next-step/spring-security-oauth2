package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class DefaultAuthenticationFailHandler implements AuthenticationFailHandler {
    private static final DefaultAuthenticationFailHandler INSTANCE = new DefaultAuthenticationFailHandler();

    private DefaultAuthenticationFailHandler() {
    }

    public static DefaultAuthenticationFailHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void onFail(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }
}
