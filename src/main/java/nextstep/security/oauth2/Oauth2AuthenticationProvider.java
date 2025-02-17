package nextstep.security.oauth2;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;

public class Oauth2AuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;

    public Oauth2AuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());

        return Oauth2AuthenticationToken.authenticated(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Oauth2AuthenticationToken.class.isAssignableFrom(authentication);
    }
}
