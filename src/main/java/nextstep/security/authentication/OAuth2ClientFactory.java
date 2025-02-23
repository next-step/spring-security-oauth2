package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;

import java.util.Map;
import java.util.Optional;

public class OAuth2ClientFactory {
    private final Map<RequestMatcher, OAuth2Client> factory;

    public OAuth2ClientFactory(Map<RequestMatcher, OAuth2Client> factory) {
        this.factory = factory;
    }

    public OAuth2Client getOAuth2Client(HttpServletRequest request) {
        for (RequestMatcher matcher : factory.keySet()) {
            if (matcher.matches(request)) {
                return factory.get(matcher);
            }
        }

        return null;
    }
}
