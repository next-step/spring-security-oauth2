package nextstep.security.userservice;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);
    UserDetails addNewMemberByOAuth2(String email);
}
