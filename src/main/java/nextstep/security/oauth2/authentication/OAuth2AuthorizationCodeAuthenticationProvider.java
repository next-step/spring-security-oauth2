package nextstep.security.oauth2.authentication;

import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.oauth2.endpoint.OAuth2AccessToken;
import nextstep.security.oauth2.endpoint.OAuth2RemoteClientAdapter;
import nextstep.security.oauth2.exception.OAuth2AuthenticationException;
import nextstep.security.oauth2.exchange.OAuth2AuthorizationRequest;
import nextstep.security.oauth2.exchange.OAuth2AuthorizationResponse;

public class OAuth2AuthorizationCodeAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2RemoteClientAdapter remoteClientAdapter;

    public OAuth2AuthorizationCodeAuthenticationProvider(OAuth2RemoteClientAdapter remoteClientAdapter) {
        this.remoteClientAdapter = remoteClientAdapter;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // 요청 상태에서는 Access Token이 없는 상태
        OAuth2AuthorizationCodeAuthenticationToken authorizationCodeAuthentication =
                (OAuth2AuthorizationCodeAuthenticationToken) authentication;
        OAuth2AuthorizationRequest authorizationRequest = authorizationCodeAuthentication.getAuthorizationExchange()

                                                                                         .getAuthorizationRequest();

        OAuth2AuthorizationResponse authorizationResponse = authorizationCodeAuthentication.getAuthorizationExchange()
                                                                                           .getAuthorizationResponse();

        if (!authorizationResponse.getState().equals(authorizationRequest.getState())) {
            throw new OAuth2AuthenticationException("Invalid State");
        }

        OAuth2AccessToken accessToken = remoteClientAdapter.getAccessToken(authorizationCodeAuthentication.getClientRegistration(),
                                                                           authorizationCodeAuthentication.getCode());

        return OAuth2AuthorizationCodeAuthenticationToken.authenticated(authorizationCodeAuthentication.getClientRegistration(),
                                                                        authorizationCodeAuthentication.getAuthorizationExchange(),
                                                                        accessToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthorizationCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
