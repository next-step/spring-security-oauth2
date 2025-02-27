package nextstep.oauth;

import nextstep.oauth.user.OAuth2User;
import nextstep.oauth.user.OAuth2UserRequest;
import nextstep.oauth.user.OAuth2UserService;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;

public class OAuth2LoginAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2AuthorizationCodeAuthenticationProvider authorizationCodeAuthenticationProvider = new OAuth2AuthorizationCodeAuthenticationProvider();

    private final OAuth2UserService userService;

    public OAuth2LoginAuthenticationProvider(OAuth2UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2LoginAuthenticationToken loginAuthenticationToken = (OAuth2LoginAuthenticationToken) authentication;

        OAuth2AuthorizationCodeAuthenticationToken authorizationCodeAuthenticationToken =
                new OAuth2AuthorizationCodeAuthenticationToken(loginAuthenticationToken.getClientRegistration()
                        , loginAuthenticationToken.getoAuth2AuthorizationResponse());

        OAuth2AuthorizationCodeAuthenticationToken authorizationCodeAuthenticatedToken
                = (OAuth2AuthorizationCodeAuthenticationToken) authorizationCodeAuthenticationProvider.authenticate(authorizationCodeAuthenticationToken);

        ClientRegistration clientRegistration = authorizationCodeAuthenticatedToken.getClientRegistration();
        String accessToken = authorizationCodeAuthenticatedToken.getAccessToken();

        OAuth2User oauth2User = this.userService.loadUser(new OAuth2UserRequest(clientRegistration, accessToken));

        return new OAuth2AuthenticationToken(oauth2User);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
