package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;

import java.util.Map;

public class OAuth2ClientFactory {

    private final Map<RequestMatcher, OAuth2Client> loginFactory;
    private final Map<RequestMatcher, OAuth2Client> authenticationFactory;

    public OAuth2ClientFactory(Map<RequestMatcher, OAuth2Client> loginFactory, Map<RequestMatcher, OAuth2Client> factory) {
        this.loginFactory = loginFactory;
        this.authenticationFactory = factory;
    }

    public OAuth2Client getOAuth2ClientForLogin(HttpServletRequest request) {
        for (RequestMatcher matcher : loginFactory.keySet()) {
            if (matcher.matches(request)) {
                return loginFactory.get(matcher);
            }
        }

        return null;
    }


    public OAuth2Client getOAuth2Client(HttpServletRequest request) {
        for (RequestMatcher matcher : authenticationFactory.keySet()) {
            if (matcher.matches(request)) {
                return authenticationFactory.get(matcher);
            }
        }

        return null;
    }
}
