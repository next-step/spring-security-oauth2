package nextstep.security.userservice;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);
}
