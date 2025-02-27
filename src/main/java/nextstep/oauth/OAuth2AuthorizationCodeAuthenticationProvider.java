package nextstep.oauth;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;

public class OAuth2AuthorizationCodeAuthenticationProvider implements AuthenticationProvider {
    private final OAuth2AccessTokenResponseClient accessTokenResponseClient = new OAuth2AccessTokenResponseClient();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2AuthorizationCodeAuthenticationToken authorizationCodeAuthenticationToken = (OAuth2AuthorizationCodeAuthenticationToken) authentication;

        ClientRegistration clientRegistration = authorizationCodeAuthenticationToken.getClientRegistration();
        OAuth2AuthorizationResponse oAuth2AuthorizationResponse = authorizationCodeAuthenticationToken.getoAuth2AuthorizationResponse();

        String accessToken = this.accessTokenResponseClient.getAccessToken(clientRegistration, oAuth2AuthorizationResponse);

        return new OAuth2AuthorizationCodeAuthenticationToken(clientRegistration, oAuth2AuthorizationResponse, accessToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
