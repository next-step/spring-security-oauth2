package nextstep.security.oauth2.registration;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class ClientRegistration {
    private final String registrationId;
    private final String clientId;
    private final String responseType;
    private final String redirectUri;
    private final String scope;
    private final String clientSecret;
    private final String tokenUri;
    private final String userInfoUri;
    private final String authorizationUri;

    private ClientRegistration(Builder builder) {
        this.registrationId = builder.registrationId;
        this.clientId = builder.clientId;
        this.responseType = builder.responseType;
        this.redirectUri = builder.redirectUri;
        this.scope = builder.scope;
        this.clientSecret = builder.clientSecret;
        this.tokenUri = builder.tokenUri;
        this.userInfoUri = builder.userInfoUri;
        this.authorizationUri = builder.authorizationUri;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public UriComponents getOauth2AuthorizationRedirectURl() {
        return UriComponentsBuilder.fromHttpUrl(authorizationUri)
                .queryParam("client_id", clientId)
                .queryParam("response_type", responseType)
                .queryParam("scope", scope)
                .queryParam("redirect_uri", redirectUri)
                .build();

    }

    public static Builder builder(String registrationId) {
        return new Builder(registrationId);
    }

    public static class Builder {
        private final String registrationId;
        private String clientId;
        private String responseType;
        private String redirectUri;
        private String scope;
        private String clientSecret;
        private String tokenUri;
        private String userInfoUri;
        private String authorizationUri;

        public Builder(String registrationId) {
            this.registrationId = registrationId;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder responseType(String responseType) {
            this.responseType = responseType;
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

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder tokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
            return this;
        }

        public Builder userInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
            return this;
        }

        public Builder authorizationUri(String authorizationUri) {
            this.authorizationUri = authorizationUri;
            return this;
        }

        public ClientRegistration build() {
            return new ClientRegistration(this);
        }
    }
}
