package nextstep.security.oauth2.authentication;

import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.user.OAuth2User;

import java.util.Set;

public class OAuth2AuthenticationToken implements Authentication {
    private final OAuth2User principal;
    private final Set<String> authorities;
    private final String authorizedClientRegistrationId;

    private final boolean authenticated;

    public static OAuth2AuthenticationToken authenticated(OAuth2User principal, Set<String> authorities,
                                                          String authorizedClientRegistrationId) {
        return new OAuth2AuthenticationToken(principal, authorities, authorizedClientRegistrationId, true);
    }

    public OAuth2AuthenticationToken(OAuth2User principal, Set<String> authorities,
                                     String authorizedClientRegistrationId, boolean authenticated) {

        this.principal = principal;
        this.authorities = authorities;
        authorities.addAll(principal.getRoles());

        this.authorizedClientRegistrationId = authorizedClientRegistrationId;
        this.authenticated = authenticated;
    }

    public OAuth2User getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public String getName() {
        return principal.getName();
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public String getAuthorizedClientRegistrationId() {
        return authorizedClientRegistrationId;
    }
}
