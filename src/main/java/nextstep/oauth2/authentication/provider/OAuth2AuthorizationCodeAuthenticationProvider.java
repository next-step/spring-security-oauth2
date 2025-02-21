package nextstep.oauth2.authentication.provider;

import nextstep.oauth2.authentication.OAuth2AccessToken;
import nextstep.oauth2.authentication.token.OAuth2AuthorizationCodeAuthenticationToken;
import nextstep.oauth2.endpoint.OAuth2AccessTokenResponseClient;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationCodeGrantRequest;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationExchange;
import nextstep.oauth2.exception.OAuth2AuthenticationException;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;

public class OAuth2AuthorizationCodeAuthenticationProvider implements AuthenticationProvider {
    private final OAuth2AccessTokenResponseClient client = new OAuth2AccessTokenResponseClient();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final OAuth2AuthorizationCodeAuthenticationToken codeToken = (OAuth2AuthorizationCodeAuthenticationToken) authentication;
        if (!codeToken.isValid()) {
            throw new OAuth2AuthenticationException();
        }
        final ClientRegistration registration = codeToken.getClientRegistration();
        final OAuth2AuthorizationExchange exchange = codeToken.getAuthorizationExchange();
        return new OAuth2AuthorizationCodeAuthenticationToken(
                registration, exchange, getAccessToken(registration, exchange)
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthorizationCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private OAuth2AccessToken getAccessToken(
            ClientRegistration registration,
            OAuth2AuthorizationExchange exchange
    ) {
        return client.getTokenResponse(
                new OAuth2AuthorizationCodeGrantRequest(registration, exchange)
        ).accessToken();
    }
}
