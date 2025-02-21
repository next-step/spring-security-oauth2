package nextstep.oauth2.access;

import jakarta.servlet.http.HttpServletRequest;

public class OAuth2AuthorizationRequestMatcher implements OAuth2RequestMatcher {

    private static final String DEFAULT_OAUTH_BASE_REQUEST_URI = "/oauth2/authorization/";
    private final String oauthBaseRequestUri;

    public OAuth2AuthorizationRequestMatcher(String oauthBaseRequestUri) {
        this.oauthBaseRequestUri = oauthBaseRequestUri;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return request.getRequestURI().startsWith(oauthBaseRequestUri);
    }

    @Override
    public String getBaseRequestUri() {
        return this.oauthBaseRequestUri;
    }
}