package nextstep.security.userdetails;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);
    UserDetails signUpUser(UserDetails userDetails);
}
