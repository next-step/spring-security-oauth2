package nextstep.security.oauth2.client.authentication;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.oauth2.OAuth2AccessTokenResponseClient;
import nextstep.security.oauth2.client.userinfo.OAuth2UserRequest;
import nextstep.security.oauth2.client.userinfo.OAuth2UserService;
import nextstep.security.oauth2.core.OAuth2AccessToken;
import nextstep.security.oauth2.core.user.OAuth2User;

public class OAuth2LoginAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2AuthorizationCodeAuthenticationProvider authorizationCodeAuthenticationProvider;

    private final OAuth2UserService userService;

    public OAuth2LoginAuthenticationProvider(OAuth2UserService userService) {
        this.authorizationCodeAuthenticationProvider = new OAuth2AuthorizationCodeAuthenticationProvider(
                new OAuth2AccessTokenResponseClient());
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2LoginAuthenticationToken loginAuthenticationToken = (OAuth2LoginAuthenticationToken) authentication;

        OAuth2AuthorizationCodeAuthenticationToken authorizationCodeAuthenticationToken
                = (OAuth2AuthorizationCodeAuthenticationToken) this.authorizationCodeAuthenticationProvider
                .authenticate(
                        new OAuth2AuthorizationCodeAuthenticationToken(
                                loginAuthenticationToken.getClientRegistration(),
                                loginAuthenticationToken.getAuthorizationExchange()
                        )
                );

        OAuth2AccessToken accessToken = authorizationCodeAuthenticationToken.getAccessToken();
        OAuth2User oauth2User = this.userService.loadUser(new OAuth2UserRequest(
                loginAuthenticationToken.getClientRegistration(), accessToken));

        return new OAuth2LoginAuthenticationToken(
                loginAuthenticationToken.getClientRegistration(), loginAuthenticationToken.getAuthorizationExchange(),
                oauth2User, oauth2User.getAuthorities(), accessToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
