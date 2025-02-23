package nextstep.security.oauth2;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.oauth2.user.OAuth2UserRequest;
import nextstep.security.oauth2.user.OAuth2UserService;
import nextstep.security.oauth2.user.Oauth2User;

public class OAuth2AuthenticationProvider implements AuthenticationProvider {
    private final OAuth2UserService<OAuth2UserRequest, Oauth2User> oAuth2UserService;

    public OAuth2AuthenticationProvider(OAuth2UserService<OAuth2UserRequest, Oauth2User> oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2LoginAuthenticationToken oAuth2LoginAuthenticationToken = (OAuth2LoginAuthenticationToken) authentication;

        OAuth2UserRequest userRequest = new OAuth2UserRequest(oAuth2LoginAuthenticationToken.getClientRegistration(), oAuth2LoginAuthenticationToken.getAccessToken());

        Oauth2User oauth2User = oAuth2UserService.loadUser(userRequest);

        return OAuth2LoginAuthenticationToken.authenticated(
                oauth2User
                , ((OAuth2LoginAuthenticationToken) authentication).getClientRegistration()
                , (OAuth2AccessToken) authentication.getCredentials()
                , oauth2User.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
