package nextstep.app.domain;

import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;

public record OAuth2AuthorizationRecord(
        String registrationId,
        String authorizationUri,
        String state
) implements OAuth2AuthorizationRequest {
    public static OAuth2AuthorizationRecord of(
            String registrationId,
            String authorizationUri,
            String state
    ) {
        return new OAuth2AuthorizationRecord(
                registrationId, authorizationUri, state
        );
    }
}
