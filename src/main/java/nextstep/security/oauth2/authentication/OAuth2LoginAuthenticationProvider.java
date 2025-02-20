package nextstep.security.oauth2.authentication;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.oauth2.endpoint.OAuth2AccessToken;
import nextstep.security.oauth2.user.OAuth2User;
import nextstep.security.oauth2.user.OAuth2UserService;

public class OAuth2LoginAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2AuthorizationCodeAuthenticationProvider authenticationProvider;
    private final OAuth2UserService userService;

    public OAuth2LoginAuthenticationProvider(OAuth2AuthorizationCodeAuthenticationProvider authenticationProvider,
                                             OAuth2UserService userService) {

        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        OAuth2LoginAuthenticationToken loginAuthenticationToken = (OAuth2LoginAuthenticationToken) authentication;
        OAuth2AuthorizationCodeAuthenticationToken authorizationCodeAuthenticationToken =
                (OAuth2AuthorizationCodeAuthenticationToken) authenticationProvider.authenticate(
                        OAuth2AuthorizationCodeAuthenticationToken.unauthenticated(loginAuthenticationToken.getClientRegistration(),
                                                                                   loginAuthenticationToken.getAuthorizationExchange()));

        OAuth2AccessToken accessToken = authorizationCodeAuthenticationToken.getAccessToken();
        OAuth2User oAuth2User = userService.loadUser(loginAuthenticationToken.getClientRegistration().getRegistrationId(), accessToken);

        return OAuth2LoginAuthenticationToken.authenticated(oAuth2User,
                                                            loginAuthenticationToken.getClientRegistration(),
                                                            loginAuthenticationToken.getAuthorizationExchange(),
                                                            accessToken);
    }
}
