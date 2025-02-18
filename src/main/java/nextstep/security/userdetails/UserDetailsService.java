package nextstep.security.userdetails;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);
    UserDetails addNewMemberByOAuth2(String email, String name);
}
