package nextstep.security.oauth2.user;

import nextstep.security.oauth2.OAuth2AuthenticationException;

public interface OAuth2UserService<R extends OAuth2UserRequest, U extends OAuth2User> {
	U loadUser(R userRequest) throws OAuth2AuthenticationException;
}
