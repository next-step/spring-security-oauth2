package nextstep.security.oauth2.core.endpoint;

public record OAuth2AuthorizationResponse(
        String redirectUri,
        String code
) {

}
