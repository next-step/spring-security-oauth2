package nextstep.security.oauth2;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;

import java.util.Optional;

public class Oauth2AuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;

    public Oauth2AuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<UserDetails> userDetails = userDetailsService.loadUser(authentication.getPrincipal().toString());

        if (userDetails.isEmpty()) {
            userDetailsService.saveUser(authentication.getPrincipal().toString());
        }

        return Oauth2AuthenticationToken.authenticated(authentication.getPrincipal().toString(), authentication.getCredentials().toString(), authentication.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Oauth2AuthenticationToken.class.isAssignableFrom(authentication);
    }
}
