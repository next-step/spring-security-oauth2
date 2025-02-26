package nextstep.oauth2.authentication;

import nextstep.oauth2.endpoint.OAuth2AuthorizationExchange;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.security.authentication.Authentication;

import java.util.Set;

// 토큰 발급 전에는 ClientRegistration와 OAuth2AuthorizationExchange만 값이 있음
public class OAuth2AuthorizationCodeAuthenticationToken implements Authentication {
    private ClientRegistration clientRegistration;

    private OAuth2AuthorizationExchange authorizationExchange;

    private OAuth2AccessToken accessToken;

    private OAuth2AuthorizationCodeAuthenticationToken(ClientRegistration clientRegistration, OAuth2AuthorizationExchange authorizationExchange, OAuth2AccessToken accessToken) {
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.accessToken = accessToken;
    }

    public static Authentication pending(final ClientRegistration clientRegistration, final OAuth2AuthorizationExchange authorizationExchange) {
        return new OAuth2AuthorizationCodeAuthenticationToken(clientRegistration, authorizationExchange, null);
    }

    public static Authentication issued(final OAuth2AuthorizationCodeAuthenticationToken authorizationCodeAuthentication, final OAuth2AccessToken accessToken) {
        return new OAuth2AuthorizationCodeAuthenticationToken(authorizationCodeAuthentication.clientRegistration, authorizationCodeAuthentication.authorizationExchange, accessToken);
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public OAuth2AuthorizationExchange getAuthorizationExchange() {
        return authorizationExchange;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    @Override
    public Set<String> getAuthorities() {
        return Set.of();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }
}
