package nextstep.app.domain;

import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;

public record OAuth2AuthorizationRecord(
        String registrationId,
        String authorizationUri,
        String responseType,
        String clientId,
        String clientSecret,
        String redirectUri,
        String scope,
        String state
) {
    public static OAuth2AuthorizationRecord of(
            String registrationId,
            String authorizationUri,
            String responseType,
            String clientId,
            String clientSecret,
            String redirectUri,
            String scope,
            String state
    ) {
        return new OAuth2AuthorizationRecord(
                registrationId, authorizationUri, responseType, clientId,
                clientSecret, redirectUri, scope, state
        );
    }

    public OAuth2AuthorizationRequest toRequest() {
        return new OAuth2AuthorizationRequest(
                registrationId, authorizationUri, responseType, clientId,
                clientSecret, redirectUri, scope, state
        );
    }
}
