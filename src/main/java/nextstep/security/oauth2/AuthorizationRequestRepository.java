package nextstep.security.oauth2;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;

public class AuthorizationRequestRepository {
    public static final String SESSION_NAME = "$AuthorizationRequest";

    public void save(final HttpServletRequest httpServletRequest, final OAuth2AuthorizationRequest authorizationRequest) {
        var session = httpServletRequest.getSession(true);
        session.setAttribute(SESSION_NAME, authorizationRequest);
    }

    @Nullable
    public OAuth2AuthorizationRequest get(final HttpServletRequest httpServletRequest) {
        var session = httpServletRequest.getSession(false);
        if (session == null) {
            return null;
        }
        if (session.getAttribute(SESSION_NAME) instanceof OAuth2AuthorizationRequest authorizationRequest) {
            return authorizationRequest;
        }
        return null;
    }
}
