package nextstep.oauth2.web.authorizationrequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationRequest;
import nextstep.oauth2.exception.UnmatchedStateException;
import nextstep.oauth2.web.OAuth2ParameterNames;

public class HttpSessionOAuth2AuthorizationRequestRepository implements OAuth2AuthorizationRequestRepository {
    private final String ATTRIBUTE_NAME = "AUTHORIZATION_REQUEST";

    private HttpSessionOAuth2AuthorizationRequestRepository() {}

    public static HttpSessionOAuth2AuthorizationRequestRepository getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        validateState(request);
        return getAuthorizationRequest(request);
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (authorizationRequest == null) {
            request.getSession().removeAttribute(ATTRIBUTE_NAME);
            return;
        }
        request.getSession().setAttribute(ATTRIBUTE_NAME, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request, HttpServletResponse response
    ) {
        final OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        request.getSession().removeAttribute(ATTRIBUTE_NAME);
        return authorizationRequest;
    }

    private void validateState(HttpServletRequest request) {
        final String httpState = request.getParameter(OAuth2ParameterNames.STATE);
        final OAuth2AuthorizationRequest authorizationRequest = getAuthorizationRequest(request);
        if (
                httpState != null && authorizationRequest != null
                        && !httpState.equals(authorizationRequest.state())
        ) {
            throw new UnmatchedStateException();
        }
    }

    private OAuth2AuthorizationRequest getAuthorizationRequest(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        return session == null ? null
                : (OAuth2AuthorizationRequest) session.getAttribute(this.ATTRIBUTE_NAME);
    }

    private static class SingletonHolder {
        private static final HttpSessionOAuth2AuthorizationRequestRepository INSTANCE = new HttpSessionOAuth2AuthorizationRequestRepository();
    }
}
