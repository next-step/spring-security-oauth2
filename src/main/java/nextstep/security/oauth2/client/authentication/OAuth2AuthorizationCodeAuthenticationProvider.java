package nextstep.security.oauth2.client.authentication;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.oauth2.OAuth2AccessTokenResponseClient;
import nextstep.security.oauth2.client.OAuth2AuthorizationCodeGrantRequest;
import nextstep.security.oauth2.core.OAuth2AuthorizationRequest;
import nextstep.security.oauth2.core.OAuth2AuthorizationResponse;
import org.springframework.util.Assert;

public class OAuth2AuthorizationCodeAuthenticationProvider implements AuthenticationProvider {
    private final OAuth2AccessTokenResponseClient accessTokenResponseClient;

    public OAuth2AuthorizationCodeAuthenticationProvider(OAuth2AccessTokenResponseClient accessTokenResponseClient) {
        Assert.notNull(accessTokenResponseClient, "accessTokenResponseClient cannot be null");
        this.accessTokenResponseClient = accessTokenResponseClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2AuthorizationCodeAuthenticationToken authorizationCodeAuthentication = (OAuth2AuthorizationCodeAuthenticationToken) authentication;

        OAuth2AccessTokenResponse accessTokenResponse = this.accessTokenResponseClient.getTokenResponse(
                new OAuth2AuthorizationCodeGrantRequest(authorizationCodeAuthentication.getClientRegistration(),
                        authorizationCodeAuthentication.getAuthorizationExchange()));

        return new OAuth2AuthorizationCodeAuthenticationToken(
                authorizationCodeAuthentication.getClientRegistration(),
                authorizationCodeAuthentication.getAuthorizationExchange(), accessTokenResponse.getAccessToken());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthorizationCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
