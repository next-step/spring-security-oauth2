package nextstep.security.oauth2.login;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.oauth2.OAuth2AccessTokenRequestProvider;

public class OAuth2AuthorizationCodeAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2AccessTokenRequestProvider oAuth2AccessTokenRequestProvider = new OAuth2AccessTokenRequestProvider();


    @Override
    public Authentication authenticate(final Authentication authentication) {
        if (authentication instanceof OAuth2AuthorizationCodeAuthenticationToken token) {
            return createOAuth2AuthorizationCodeAuthenticationToken(token);
        }
        return null;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication == OAuth2AuthorizationCodeAuthenticationToken.class;
    }

    private OAuth2AuthorizationCodeAuthenticationToken createOAuth2AuthorizationCodeAuthenticationToken(final OAuth2AuthorizationCodeAuthenticationToken token) {
        try {
            var accessToken = oAuth2AccessTokenRequestProvider.getAccessToken(
                    token.getRegistration(), token.getPrincipal());
            return OAuth2AuthorizationCodeAuthenticationToken.authenticated(
                    token.getPrincipal(),
                    token.getRegistration(),
                    accessToken);
        } catch (Exception ex) {
            return OAuth2AuthorizationCodeAuthenticationToken.unauthenticated(
                    token.getPrincipal(),
                    token.getRegistration());
        }
    }


}
