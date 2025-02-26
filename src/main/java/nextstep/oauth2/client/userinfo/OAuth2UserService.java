package nextstep.oauth2.client.userinfo;

@FunctionalInterface
public interface OAuth2UserService {
    OAuth2User loadUser(OAuth2UserRequest userRequest);
}
