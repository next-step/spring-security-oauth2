package nextstep.security.authentication.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.Authentication;

import java.io.IOException;

public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private DefaultAuthenticationSuccessHandler() {}

    public static DefaultAuthenticationSuccessHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        response.sendRedirect("/");
    }

    private static class SingletonHolder {
        private static final DefaultAuthenticationSuccessHandler INSTANCE = new DefaultAuthenticationSuccessHandler();
    }
}
