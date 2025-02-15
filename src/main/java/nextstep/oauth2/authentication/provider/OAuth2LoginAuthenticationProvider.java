package nextstep.oauth2.authentication.provider;

import nextstep.oauth2.authentication.OAuth2AccessToken;
import nextstep.oauth2.authentication.token.OAuth2AuthorizationCodeAuthenticationToken;
import nextstep.oauth2.authentication.token.OAuth2LoginAuthenticationToken;
import nextstep.oauth2.userinfo.OAuth2User;
import nextstep.oauth2.userinfo.OAuth2UserRequest;
import nextstep.oauth2.userinfo.OAuth2UserService;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;

public class OAuth2LoginAuthenticationProvider implements AuthenticationProvider {
    private final OAuth2AuthorizationCodeAuthenticationProvider provider = new OAuth2AuthorizationCodeAuthenticationProvider();
    private final OAuth2UserService userService;

    public OAuth2LoginAuthenticationProvider(OAuth2UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final OAuth2LoginAuthenticationToken loginToken = (OAuth2LoginAuthenticationToken) authentication;
        final OAuth2AccessToken accessToken = getAccessToken(loginToken);
        final OAuth2User oauth2User = loadUser(loginToken, accessToken);
        return new OAuth2LoginAuthenticationToken(
                loginToken.getClientRegistration(), loginToken.getAuthorizationExchange(),
                oauth2User, oauth2User.authorities(), accessToken
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private OAuth2AccessToken getAccessToken(OAuth2LoginAuthenticationToken loginToken) {
        return ((OAuth2AuthorizationCodeAuthenticationToken) provider.authenticate(
                new OAuth2AuthorizationCodeAuthenticationToken(
                        loginToken.getClientRegistration(),
                        loginToken.getAuthorizationExchange()
                )
        )).getAccessToken();
    }

    private OAuth2User loadUser(OAuth2LoginAuthenticationToken loginToken, OAuth2AccessToken accessToken) {
        return userService.loadUser(
                new OAuth2UserRequest(loginToken.getClientRegistration(), accessToken)
        );
    }
}
