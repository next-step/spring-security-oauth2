package nextstep.security.oauth2user;

import nextstep.security.oauth2user.OAuth2User;

import java.util.Set;

public class BaseOauth2User implements OAuth2User {

    private String name;

    private Set<String> authorities;

    public BaseOauth2User(String name, Set<String> authorities) {
        this.name = name;
        this.authorities = authorities;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }
}
