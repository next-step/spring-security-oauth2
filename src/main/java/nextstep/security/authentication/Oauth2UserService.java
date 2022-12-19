package nextstep.security.authentication;

import nextstep.security.exception.AuthenticationException;

public interface Oauth2UserService {
    OAuth2User loadUser(String accessToken) throws AuthenticationException;
}
