package nextstep.security.oauth2.login;

import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.ClientRegistration;
import nextstep.security.oauth2.OAuth2AuthorizationRequest;
import nextstep.security.oauth2.userdetails.OAuth2UserDetails;

import java.util.Set;

public class OAuth2LoginAuthenticationToken implements Authentication {

    private final String code;

    private final boolean authenticated;

    private final Set<String> authorities;

    private final OAuth2AuthorizationRequest oAuth2AuthorizationRequest;

    private final OAuth2UserDetails oAuth2UserDetails;


    public OAuth2LoginAuthenticationToken(final String code,
                                          final boolean authenticated,
                                          final Set<String> authorities,
                                          final OAuth2AuthorizationRequest oAuth2AuthorizationRequest,
                                          final OAuth2UserDetails oAuth2UserDetails
    ) {
        this.code = code;
        this.authenticated = authenticated;
        this.authorities = authorities;
        this.oAuth2AuthorizationRequest = oAuth2AuthorizationRequest;
        this.oAuth2UserDetails = oAuth2UserDetails;
    }

    public static OAuth2LoginAuthenticationToken authenticated(final OAuth2UserDetails oAuth2UserDetails) {
        return new OAuth2LoginAuthenticationToken(
                oAuth2UserDetails.getUsername(),
                true,
                oAuth2UserDetails.getAuthorities(),
                null,
                oAuth2UserDetails);
    }

    public static OAuth2LoginAuthenticationToken unauthenticated(final String principal,
                                                                 final OAuth2AuthorizationRequest request
    ) {
        return new OAuth2LoginAuthenticationToken(principal,
                false,
                null,
                request,
                null);
    }

    @Override
    public Set<String> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getCredentials() {
        return this.code;
    }

    @Override
    public String getPrincipal() {
        return this.code;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public ClientRegistration getRegistration() {
        return this.oAuth2AuthorizationRequest.getRegistration();
    }

    public OAuth2UserDetails getOAuth2UserDetails() {
        return oAuth2UserDetails;
    }
}
