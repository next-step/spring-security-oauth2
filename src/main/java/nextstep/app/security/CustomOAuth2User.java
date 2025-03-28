package nextstep.app.security;

import nextstep.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {

    private final String username;

    public CustomOAuth2User(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }
}
