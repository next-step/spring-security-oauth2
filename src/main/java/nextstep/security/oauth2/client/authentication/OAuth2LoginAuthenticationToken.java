package nextstep.security.oauth2.client.authentication;

import java.util.Collections;
import java.util.Set;
import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.core.OAuth2AccessToken;
import nextstep.security.oauth2.core.OAuth2AuthorizationExchange;
import nextstep.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

public class OAuth2LoginAuthenticationToken implements Authentication {
    private final ClientRegistration clientRegistration;
    private final OAuth2AuthorizationExchange authorizationExchange;
    private OAuth2User principal;
    private OAuth2AccessToken accessToken;
    private boolean authenticated;

    public OAuth2LoginAuthenticationToken(ClientRegistration clientRegistration,
                                          OAuth2AuthorizationExchange authorizationExchange) {
        Assert.notNull(clientRegistration, "clientRegistration cannot be null");
        Assert.notNull(authorizationExchange, "authorizationExchange cannot be null");
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.setAuthenticated(false);
    }

    public OAuth2LoginAuthenticationToken(ClientRegistration clientRegistration,
                                          OAuth2AuthorizationExchange authorizationExchange, OAuth2User principal,
                                          OAuth2AccessToken accessToken) {
        Assert.notNull(clientRegistration, "clientRegistration cannot be null");
        Assert.notNull(authorizationExchange, "authorizationExchange cannot be null");
        Assert.notNull(principal, "principal cannot be null");
        Assert.notNull(accessToken, "accessToken cannot be null");
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.principal = principal;
        this.accessToken = accessToken;
        this.setAuthenticated(true);
    }


    @Override
    public OAuth2User getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Set<String> getAuthorities() {
        return Collections.emptySet();
    }

    public ClientRegistration getClientRegistration() {
        return this.clientRegistration;
    }

    public OAuth2AuthorizationExchange getAuthorizationExchange() {
        return this.authorizationExchange;
    }

    public OAuth2AccessToken getAccessToken() {
        return this.accessToken;
    }
}
