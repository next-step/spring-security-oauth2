package nextstep.security.authentication.oauth;

import nextstep.security.userservice.OAuth2ClientRegistration;

public record OAuth2AuthorizationRequest(
        String registrationId, // provider,
        String authorizationUri,
        String responseType,
        String clientId,
        String clientSecret,
        String redirectUri,
        String scope,
        String state
) {
    public static class Builder {
        private String registrationId;
        private String authorizationUri;
        private String responseType;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String scope;
        private String state;

        public Builder authorizationUri(String authorizationUri) {
            this.authorizationUri = authorizationUri;
            return this;
        }

        public Builder responseType(String responseType) {
            this.responseType = responseType;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public OAuth2AuthorizationRequest build() {
            return new OAuth2AuthorizationRequest(
                    registrationId,
                    authorizationUri,
                    responseType,
                    clientId,
                    clientSecret,
                    redirectUri,
                    scope,
                    state
            );
        }
    }

    public static Builder from(OAuth2ClientRegistration registration) {
        Builder builder = new Builder();
        builder.registrationId = registration.getRegistrationId();
        builder.clientId = registration.getClientId();
        builder.clientSecret = registration.getClientSecret();
        builder.redirectUri = registration.getRedirectUri();
        builder.scope = registration.getScope();
        return builder;
    }
}
