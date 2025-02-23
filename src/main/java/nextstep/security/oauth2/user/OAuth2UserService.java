package nextstep.security.oauth2.user;

public interface OAuth2UserService {
    Oauth2User loadUser(String principal);
}
