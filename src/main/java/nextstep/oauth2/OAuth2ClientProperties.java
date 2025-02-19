package nextstep.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public class OAuth2ClientProperties {
    private final Map<String, Provider> provider = new HashMap<>();
    private final Map<String, Registration> registration = new HashMap<>();

    public Map<String, Provider> getProvider() {
        return this.provider;
    }

    public Map<String, Registration> getRegistration() {
        return this.registration;
    }

    public Registration findRegistration(final String registrationId) {
        OAuth2ClientProperties.Registration registration = this.registration.get(registrationId);

        if (registration == null) {
            throw new IllegalArgumentException("OAuth2 registration not found for '" + registrationId + "'");
        }

        return registration;
    }

    public Provider findProvider(final String registrationId) {
        Provider provider = this.provider.get(registrationId);

        if (provider == null) {
            throw new IllegalArgumentException("OAuth2 provider not found for '" + registrationId + "'");
        }

        return provider;
    }

    public static class Provider {

        /**
         * Authorization URI for the provider.
         */
        private String authorizationUri;

        /**
         * Token URI for the provider.
         */
        private String tokenUri;

        /**
         * User info URI for the provider.
         */
        private String userInfoUri;

        public String getAuthorizationUri() {
            return this.authorizationUri;
        }

        public void setAuthorizationUri(String authorizationUri) {
            this.authorizationUri = authorizationUri;
        }

        public String getTokenUri() {
            return this.tokenUri;
        }

        public void setTokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
        }

        public String getUserInfoUri() {
            return this.userInfoUri;
        }

        public void setUserInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
        }

        @Override
        public String toString() {
            return "Provider{" +
                    "authorizationUri='" + authorizationUri + '\'' +
                    ", tokenUri='" + tokenUri + '\'' +
                    ", userInfoUri='" + userInfoUri + '\'' +
                    '}';
        }
    }

    public static class Registration {

        /**
         * Reference to the OAuth 2.0 provider to use. May reference an element from the
         * 'provider' property or used one of the commonly used providers (google, github,
         * facebook, okta).
         */
        private String provider;

        /**
         * Client ID for the registration.
         */
        private String clientId;

        /**
         * Client secret of the registration.
         */
        private String clientSecret;

        /**
         * Redirect URI. May be left blank when using a pre-defined provider.
         */
        private String redirectUri;

        /**
         * Authorization scopes. When left blank the provider's default scopes, if any,
         * will be used.
         */
        private Set<String> scope;

        public String getProvider() {
            return this.provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return this.clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getRedirectUri() {
            return this.redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }

        public Set<String> getScope() {
            return this.scope;
        }

        public void setScope(Set<String> scope) {
            this.scope = scope;
        }

        @Override
        public String toString() {
            return "Registration{" +
                    "provider='" + provider + '\'' +
                    ", clientId='" + clientId + '\'' +
                    ", clientSecret='" + clientSecret + '\'' +
                    ", redirectUri='" + redirectUri + '\'' +
                    ", scope=" + scope +
                    '}';
        }
    }
}
