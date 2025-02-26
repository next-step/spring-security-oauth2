package nextstep.security.oauth2.core;

import nextstep.security.authentication.AuthenticationException;

public class OAuth2AuthenticationException extends AuthenticationException {

    public OAuth2AuthenticationException() {}

    public OAuth2AuthenticationException(String message) {
        super(message);
    }
}
