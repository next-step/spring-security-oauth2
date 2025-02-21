package nextstep.oauth2.client;

import java.util.Collections;
import java.util.Set;

public class ClientRegistration {
    private final String registrationId;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final Set<String> scopes;
    private final ProviderDetails providerDetails;
    private final String clientName;

    private ClientRegistration(Builder builder) {
        this.registrationId = builder.registrationId;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.redirectUri = builder.redirectUri;
        this.scopes = builder.scopes;
        this.providerDetails = builder.providerDetails;
        this.clientName = builder.clientName;
    }

    public static Builder builder() {
        return new Builder();
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

    public Set<String> getScopes() {
        return scopes;
    }

    public ProviderDetails getProviderDetails() {
        return providerDetails;
    }

    public String getClientName() {
        return clientName;
    }

    public String getAuthorizationUri() {
        return providerDetails.getAuthorizationUri();
    }

    public String getTokenUri() {
        return providerDetails.getTokenUri();
    }

    public String getUserInfoUri() {
        return providerDetails.getUserInfoUri();
    }

    public static class Builder {
        private String registrationId;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private Set<String> scopes = Collections.emptySet();
        private ProviderDetails providerDetails = new ProviderDetails();
        private String clientName;

        public Builder registrationId(String registrationId) {
            this.registrationId = registrationId;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder scopes(Set<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder providerDetails(ProviderDetails providerDetails) {
            this.providerDetails = providerDetails;
            return this;
        }

        public Builder clientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        public ClientRegistration build() {
            return new ClientRegistration(this);
        }
    }

    public static class ProviderDetails {
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;

        public ProviderDetails() {
        }

        public ProviderDetails(String authorizationUri, String tokenUri, String userInfoUri) {
            this.authorizationUri = authorizationUri;
            this.tokenUri = tokenUri;
            this.userInfoUri = userInfoUri;
        }

        public String getAuthorizationUri() {
            return authorizationUri;
        }

        public String getTokenUri() {
            return tokenUri;
        }

        public String getUserInfoUri() {
            return userInfoUri;
        }
    }
}
