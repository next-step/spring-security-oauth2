package nextstep.security.oauth2.user;

import nextstep.security.oauth2.endpoint.OAuth2AccessToken;

public interface OAuth2UserService {

    OAuth2User loadUser(String registrationId, OAuth2AccessToken accessToken);
}
