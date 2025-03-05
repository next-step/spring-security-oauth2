package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@FunctionalInterface
public interface AuthenticationSuccessHandler {

    void onSuccess(final HttpServletRequest request, final HttpServletResponse response, Authentication authentication) throws IOException;
}
