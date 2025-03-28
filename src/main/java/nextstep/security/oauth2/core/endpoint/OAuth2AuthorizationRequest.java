package nextstep.security.oauth2.core.endpoint;

public record OAuth2AuthorizationRequest(
        String registrationId,
        String redirectUri
) {

}
