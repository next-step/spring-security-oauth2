package nextstep.oauth;

import jakarta.servlet.http.HttpServletRequest;

public class AuthorizationRequestRepository {
    private static final String OAUTH_REQUEST_SESSION_KEY = "oauthRequestSession";

    public void saveAuthorizationRequest(HttpServletRequest request
            , OAuth2AuthorizationRequest authorizationRequest) {
        request.getSession().setAttribute(OAUTH_REQUEST_SESSION_KEY, authorizationRequest);
    }

    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return (OAuth2AuthorizationRequest) request.getSession().getAttribute(OAUTH_REQUEST_SESSION_KEY);
    }
}
