package nextstep.oauth2.authentication;

import nextstep.oauth2.endpoint.OAuth2AccessTokenResponse;
import nextstep.oauth2.endpoint.OAuth2AuthorizationCodeGrantRequest;
import nextstep.oauth2.endpoint.OAuth2AuthorizationRequest;
import nextstep.oauth2.endpoint.OAuth2AuthorizationResponse;
import nextstep.oauth2.endpoint.OAuth2AccessTokenResponseClient;
import nextstep.oauth2.exception.OAuth2AuthorizationException;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;

public class OAuth2AuthorizationCodeAuthenticationProvider implements AuthenticationProvider {
    private final OAuth2AccessTokenResponseClient accessTokenResponseClient = new OAuth2AccessTokenResponseClient();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2AuthorizationCodeAuthenticationToken authorizationCodeAuthentication = (OAuth2AuthorizationCodeAuthenticationToken) authentication;
        OAuth2AuthorizationResponse authorizationResponse = authorizationCodeAuthentication.getAuthorizationExchange()
                .getAuthorizationResponse();
        if (authorizationResponse.statusError()) {
            throw new OAuth2AuthorizationException();
        }
        OAuth2AuthorizationRequest authorizationRequest = authorizationCodeAuthentication.getAuthorizationExchange()
                .getAuthorizationRequest();
        if (!authorizationResponse.getState().equals(authorizationRequest.getState())) {
            throw new OAuth2AuthorizationException();
        }
        OAuth2AccessTokenResponse accessTokenResponse = this.accessTokenResponseClient.getTokenResponse(
                new OAuth2AuthorizationCodeGrantRequest(authorizationCodeAuthentication.getClientRegistration(),
                        authorizationCodeAuthentication.getAuthorizationExchange()));

        return OAuth2AuthorizationCodeAuthenticationToken.issued(authorizationCodeAuthentication, accessTokenResponse.getAccessToken());
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return OAuth2AuthorizationCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
