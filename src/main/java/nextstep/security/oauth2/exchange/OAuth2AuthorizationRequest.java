package nextstep.security.oauth2.exchange;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

public final class OAuth2AuthorizationRequest {

    private String registrationId;

    private String clientId;

    private String redirectUri;

    private String state;

    private Set<String> scopes;

    private String authorizationRequestUri;

    public String getRegistrationId() {
        return registrationId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getState() {
        return state;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public String getAuthorizationRequestUri() {
        return authorizationRequestUri;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String registrationId;
        private String clientId;
        private String redirectUri;
        private Set<String> scopes;
        private String state;
        private String authorizationRequestUri;

        public Builder registrationId(String registrationId) {
            this.registrationId = registrationId;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
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

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder authorizationRequestUri(String authorizationRequestUri) {
            this.authorizationRequestUri = authorizationRequestUri;
            return this;
        }

        public OAuth2AuthorizationRequest build() {
            OAuth2AuthorizationRequest oAuth2AuthorizationRequest = new OAuth2AuthorizationRequest();
            oAuth2AuthorizationRequest.registrationId = this.registrationId;
            oAuth2AuthorizationRequest.clientId = this.clientId;
            oAuth2AuthorizationRequest.scopes = this.scopes;
            oAuth2AuthorizationRequest.redirectUri = this.redirectUri;
            oAuth2AuthorizationRequest.state = this.state;
            oAuth2AuthorizationRequest.authorizationRequestUri = this.authorizationRequestUri;
            return oAuth2AuthorizationRequest;
        }
    }
}
