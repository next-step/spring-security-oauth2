package nextstep.security.authentication;

public interface OAuth2UserService {
    OAuth2User loadUser(OAuth2UserRequest userRequest);
}
