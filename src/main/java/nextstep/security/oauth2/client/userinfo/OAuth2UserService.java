package nextstep.security.oauth2.client.userinfo;

import nextstep.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserService {
    OAuth2User loadUser(OAuth2UserRequest userRequest);
}
