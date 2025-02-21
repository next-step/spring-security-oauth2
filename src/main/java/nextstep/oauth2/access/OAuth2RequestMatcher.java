package nextstep.oauth2.access;

import nextstep.security.access.RequestMatcher;

public interface OAuth2RequestMatcher extends RequestMatcher {
    String getBaseRequestUri();
}