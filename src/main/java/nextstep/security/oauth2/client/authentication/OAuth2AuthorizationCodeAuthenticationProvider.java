package nextstep.security.oauth2.client.authentication;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.oauth2.client.endpoint.OAuth2AccessTokenResponse;
import nextstep.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import nextstep.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;

public class OAuth2AuthorizationCodeAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2AccessTokenResponseClient accessTokenResponseClient = new OAuth2AccessTokenResponseClient();

    public OAuth2AuthorizationCodeAuthenticationProvider() {
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthorizationCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2AuthorizationCodeAuthenticationToken codeAuthenticationToken =
                (OAuth2AuthorizationCodeAuthenticationToken) authentication;

        OAuth2AuthorizationCodeGrantRequest codeGrantRequest = new OAuth2AuthorizationCodeGrantRequest(
                codeAuthenticationToken.getClientRegistration(),
                codeAuthenticationToken.getAuthorizationExchange()
        );

        OAuth2AccessTokenResponse tokenResponse = accessTokenResponseClient.getTokenResponse(codeGrantRequest);

        return OAuth2AuthorizationCodeAuthenticationToken.authenticated(
                tokenResponse.accessToken(),
                codeAuthenticationToken.getClientRegistration(),
                codeAuthenticationToken.getAuthorizationExchange()
        );
    }
}
