package nextstep.oauth2.authentication;

import nextstep.oauth2.client.userinfo.OAuth2UserService;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;

public class OAuth2LoginAuthenticationProvider implements AuthenticationProvider {
    public OAuth2LoginAuthenticationProvider(final OAuth2UserService oAuth2UserService) {

    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return false;
    }
}
