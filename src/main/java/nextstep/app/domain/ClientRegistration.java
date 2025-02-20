package nextstep.app.domain;

import nextstep.security.userservice.OAuth2ClientRegistration;

import java.util.Set;

public record ClientRegistration(
        String registrationId, // provider
        String clientId,
        String clientSecret,
        String redirectUri,
        Set<String> scope
) implements OAuth2ClientRegistration {

    @Override
    public String getRegistrationId() {
        return registrationId;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public String getRedirectUri() {
        return this.redirectUri;
    }

    @Override
    public Set<String> getScope() {
        return this.scope;
    }

    public static class Builder {
        private String registrationId;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private Set<String> scope;

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

        public Builder scope(Set<String> scope) {
            this.scope = scope;
            return this;
        }

        public ClientRegistration build() {
            return new ClientRegistration(
                    registrationId,
                    clientId,
                    clientSecret,
                    redirectUri,
                    scope
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
