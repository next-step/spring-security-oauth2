package nextstep.app.application.dto;

import nextstep.security.userdetails.UserDetails;

import java.util.Set;

public record UserDetailsImpl(
        String email,
        String password,
        Set<String> roles
) implements UserDetails {

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
        return roles;
    }
}
