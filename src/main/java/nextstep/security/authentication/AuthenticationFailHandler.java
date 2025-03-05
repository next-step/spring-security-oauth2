package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;

import java.io.IOException;

@FunctionalInterface
public interface AuthenticationFailHandler {
    void onFail(final HttpServletRequest request, final HttpServletResponse response, AuthenticationException exception) throws IOException;
}
