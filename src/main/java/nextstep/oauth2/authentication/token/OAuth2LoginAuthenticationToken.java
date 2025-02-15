package nextstep.oauth2.authentication.token;

import nextstep.oauth2.authentication.OAuth2AccessToken;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationExchange;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.userinfo.OAuth2User;
import nextstep.security.authentication.Authentication;

import java.util.Collections;
import java.util.Set;

public final class OAuth2LoginAuthenticationToken implements Authentication {
    private final ClientRegistration clientRegistration;
    private final OAuth2AuthorizationExchange authorizationExchange;
    private final Set<String> authorities;
    private final boolean authenticated;
    private final OAuth2User principal;
    private final OAuth2AccessToken accessToken;

    private OAuth2LoginAuthenticationToken(
            ClientRegistration clientRegistration,
            OAuth2AuthorizationExchange authorizationExchange,
            Set<String> authorities,
            boolean authenticated,
            OAuth2User principal,
            OAuth2AccessToken accessToken
    ) {
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.authorities = authorities;
        this.authenticated = authenticated;
        this.principal = principal;
        this.accessToken = accessToken;
    }

    public OAuth2LoginAuthenticationToken(
            ClientRegistration clientRegistration,
            OAuth2AuthorizationExchange authorizationExchange,
            OAuth2User principal,
            Set<String> authorities,
            OAuth2AccessToken accessToken
    ) {
        this(
                clientRegistration,
                authorizationExchange,
                authorities,
                true,
                principal,
                accessToken
        );
    }

    public OAuth2LoginAuthenticationToken(
            ClientRegistration clientRegistration,
            OAuth2AuthorizationExchange authorizationExchange
    ) {
        this(
                clientRegistration,
                authorizationExchange,
                Collections.emptySet(),
                false,
                null,
                null
        );
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
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
}
