package nextstep.security.oauth2.client.authentication;

import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import nextstep.security.oauth2.core.user.OAuth2User;

import java.util.Set;

public class OAuth2LoginAuthenticationToken implements Authentication {

    private OAuth2User oauth2User;
    private final ClientRegistration clientRegistration;
    private final OAuth2AuthorizationExchange authorizationExchange;
    private final boolean authenticated;

    private OAuth2LoginAuthenticationToken(
            OAuth2User oauth2User,
            ClientRegistration clientRegistration,
            OAuth2AuthorizationExchange authorizationExchange,
            boolean authenticated
    ) {
        this.oauth2User = oauth2User;
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.authenticated = authenticated;
    }

    public static OAuth2LoginAuthenticationToken unauthenticated(
            ClientRegistration clientRegistration,
            OAuth2AuthorizationExchange authorizationExchange
    ) {
        return new OAuth2LoginAuthenticationToken(null, clientRegistration, authorizationExchange, false);
    }

    public static OAuth2LoginAuthenticationToken authenticated(OAuth2User oAuth2User) {
        return new OAuth2LoginAuthenticationToken(oAuth2User, null, null, true);
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public Object getPrincipal() {
        return this.oauth2User;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Set<String> getAuthorities() {
        return null; // todo: fill authorities
    }

    public ClientRegistration getClientRegistration() {
        return this.clientRegistration;
    }

    public OAuth2AuthorizationExchange getAuthorizationExchange() {
        return this.authorizationExchange;
    }
}
