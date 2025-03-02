package nextstep.oauth.user;

public interface OAuth2UserService {
    OAuth2User loadUser(OAuth2UserRequest userRequest);
}
