package nextstep.app.application;

import nextstep.security.oauth2.user.OAuth2User;

import java.util.Set;

public class CustomOAuth2User implements OAuth2User {
    private final String email;
    private final Set<String> authorities;

    public CustomOAuth2User(String email, Set<String> authorities) {
        this.email = email;
        this.authorities = authorities;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }
}
