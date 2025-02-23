package nextstep.security.authentication;

import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;

import java.util.Set;

public class OAuth2AuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;

    public OAuth2AuthenticationProvider(UserDetailsService userDetailsService1) {
        this.userDetailsService = userDetailsService1;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
            return UsernamePasswordAuthenticationToken.authenticated(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        } catch (AuthenticationException e) {
            UserDetails newUserDetails = new UserDetails() {
                @Override
                public String getUsername() {
                    return authentication.getPrincipal().toString();
                }

                @Override
                public String getPassword() {
                    return "";
                }

                @Override
                public Set<String> getAuthorities() {
                    return Set.of();
                }
            };

            UserDetails newUser = userDetailsService.signUpUser(newUserDetails);
            return UsernamePasswordAuthenticationToken.authenticated(newUser.getUsername(), newUser.getPassword(), newUser.getAuthorities());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
