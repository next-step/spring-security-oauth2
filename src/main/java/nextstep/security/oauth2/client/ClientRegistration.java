package nextstep.security.oauth2.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties("oauth2.client.registration")
public class ClientRegistration {

    private final String registrationId;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String authorizationRequestUri;
    private final Set<String> scopes;

    public ClientRegistration(String registrationId, String clientId, String clientSecret,
                              String redirectUri, String authorizationRequestUri, Set<String> scopes) {

        this.registrationId = registrationId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.authorizationRequestUri = authorizationRequestUri;
        this.scopes = scopes;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getAuthorizationRequestUri() {
        return authorizationRequestUri;
    }

    public Set<String> getScopes() {
        return scopes;
    }
}
