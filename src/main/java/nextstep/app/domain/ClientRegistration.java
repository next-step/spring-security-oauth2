package nextstep.app.domain;

import nextstep.security.userservice.OAuth2ClientRegistration;

public record ClientRegistration(
        String registrationId, // provider
        String clientId,
        String clientSecret,
        String clientAuthenticationMethod,
        String redirectUri,
        String scope
) implements OAuth2ClientRegistration {

    @Override
    public String getRegistrationId() {
        return registrationId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getRedirectUri() {
        return redirectUri;
    }

    @Override
    public String getScope() {
        return scope;
    }

    public static class Builder {
        private String registrationId;
        private String clientId;
        private String clientSecret;
        private String clientAuthenticationMethod;
        private String redirectUri;
        private String scope;

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

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public ClientRegistration build() {
            return new ClientRegistration(
                    registrationId,
                    clientId,
                    clientSecret,
                    clientAuthenticationMethod,
                    redirectUri,
                    scope
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
