package nextstep.security.oauth2user;

import nextstep.security.exception.AuthenticationException;

public interface Oauth2UserService {
    OAuth2User loadUser(String accessToken) throws AuthenticationException;
}
