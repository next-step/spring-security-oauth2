package nextstep.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthorizationRequestRepository {
    private final static String DEFAULT_SESSION_KEY = "DEFAULT_SESSION_KEY";

    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            return (OAuth2AuthorizationRequest) session.getAttribute(DEFAULT_SESSION_KEY);
        }

        return null;
    }

    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            return;
        }
        request.getSession().setAttribute(DEFAULT_SESSION_KEY, authorizationRequest);
    }

    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);

        if (authorizationRequest != null) {
            request.getSession().removeAttribute(DEFAULT_SESSION_KEY);
        }

        return authorizationRequest;
    }
}
