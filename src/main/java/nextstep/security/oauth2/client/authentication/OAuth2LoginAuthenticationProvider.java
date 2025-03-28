package nextstep.security.oauth2.client.authentication;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.client.userinfo.OAuth2UserRequest;
import nextstep.security.oauth2.client.userinfo.OAuth2UserService;
import nextstep.security.oauth2.core.OAuth2AccessToken;
import nextstep.security.oauth2.core.user.OAuth2User;

public class OAuth2LoginAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2AuthorizationCodeAuthenticationProvider authorizationCodeAuthenticationProvider;
    private final OAuth2UserService userService;

    public OAuth2LoginAuthenticationProvider(OAuth2UserService userService) {
        this.authorizationCodeAuthenticationProvider = new OAuth2AuthorizationCodeAuthenticationProvider();
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2LoginAuthenticationToken token = (OAuth2LoginAuthenticationToken) authentication;

        OAuth2AccessToken accessToken = exchangeToAccessToken(token);

        OAuth2User oAuth2User = loadUser(token.getClientRegistration(), accessToken);

        return OAuth2LoginAuthenticationToken.authenticated(oAuth2User);
    }

    private OAuth2AccessToken exchangeToAccessToken(OAuth2LoginAuthenticationToken loginAuthenticationToken) {
        Authentication unAuthenticated = OAuth2AuthorizationCodeAuthenticationToken.unauthenticated(
                loginAuthenticationToken.getClientRegistration(),
                loginAuthenticationToken.getAuthorizationExchange()
        );

        OAuth2AuthorizationCodeAuthenticationToken authenticated =
                (OAuth2AuthorizationCodeAuthenticationToken) this.authorizationCodeAuthenticationProvider.authenticate(unAuthenticated);

        return authenticated.getAccessToken();
    }

    private OAuth2User loadUser(ClientRegistration clientRegistration, OAuth2AccessToken accessToken) {
        return this.userService.loadUser(new OAuth2UserRequest(
                clientRegistration,
                accessToken
        ));
    }
}
