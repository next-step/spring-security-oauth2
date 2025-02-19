package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;

public interface OAuth2ProviderSupportChecker {
    void checkRequest(HttpServletRequest request, RequestMatcher requestMatcher) throws UnsupportedOAuth2ProviderException;
}
