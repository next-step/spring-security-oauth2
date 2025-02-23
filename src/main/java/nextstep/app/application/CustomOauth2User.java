package nextstep.app.application;

import nextstep.security.oauth2.user.Oauth2User;

import java.util.Set;

public class CustomOauth2User implements Oauth2User {
    private final String email;
    private final Set<String> authorities;

    public CustomOauth2User(String email, Set<String> authorities) {
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
