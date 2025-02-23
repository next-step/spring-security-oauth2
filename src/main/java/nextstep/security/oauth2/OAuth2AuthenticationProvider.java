package nextstep.security.oauth2;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.oauth2.user.OAuth2UserService;
import nextstep.security.oauth2.user.Oauth2User;

public class OAuth2AuthenticationProvider implements AuthenticationProvider {
    private final OAuth2UserService oAuth2UserService;

    public OAuth2AuthenticationProvider(OAuth2UserService oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Oauth2User oauth2User = oAuth2UserService.loadUser(authentication.getPrincipal().toString());

        return OAuth2AuthenticationToken.authenticated(authentication.getPrincipal().toString(), authentication.getCredentials().toString(), oauth2User.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthenticationToken.class.isAssignableFrom(authentication);
    }
}
