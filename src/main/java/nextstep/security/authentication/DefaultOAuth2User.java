package nextstep.security.authentication;

public class DefaultOAuth2User implements OAuth2User {

    private String email;

    public DefaultOAuth2User(String email) {
        this.email = email;
    }

    public static DefaultOAuth2User from(UserProfile userProfile) {
        return new DefaultOAuth2User(userProfile.getEmail());
    }

    @Override
    public String getEmail() {
        return email;
    }
}
