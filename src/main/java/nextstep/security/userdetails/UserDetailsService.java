package nextstep.security.userdetails;

import java.util.Optional;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);

    void saveUser(String principal);

    Optional<UserDetails> loadUser(String principal);
}
