package nextstep.app.application;

import nextstep.security.userdetails.UserDetails;

import java.util.Set;

public class CustomUserDetails implements UserDetails {
    private final String email;
    private final String password;
    private final Set<String> authorities;

    public CustomUserDetails(String email, String password, Set<String> authorities) {
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }
}
