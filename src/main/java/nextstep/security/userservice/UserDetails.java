package nextstep.security.userservice;

import java.util.Set;

public interface UserDetails {
    String getUsername();

    String getPassword();

    Set<String> getAuthorities();
}
