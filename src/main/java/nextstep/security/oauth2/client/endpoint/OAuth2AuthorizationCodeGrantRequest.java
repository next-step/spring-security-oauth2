package nextstep.security.oauth2.client.endpoint;

import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;

public final class OAuth2AuthorizationCodeGrantRequest {

    private static final String AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code";
    private final ClientRegistration registration;
    private final OAuth2AuthorizationExchange authorizationExchange;

    public OAuth2AuthorizationCodeGrantRequest(
            ClientRegistration registration,
            OAuth2AuthorizationExchange authorizationExchange
    ) {
        this.registration = registration;
        this.authorizationExchange = authorizationExchange;
    }

    public String getAuthorizationGrantType() {
        return AUTHORIZATION_CODE_GRANT_TYPE;
    }

    public ClientRegistration getRegistration() {
        return this.registration;
    }

    public OAuth2AuthorizationExchange getAuthorizationExchange() {
        return this.authorizationExchange;
    }
}
