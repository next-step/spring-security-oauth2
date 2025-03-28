package nextstep.security.oauth2.core.endpoint;

public record OAuth2AuthorizationExchange(
        OAuth2AuthorizationRequest authorizationRequest,
        OAuth2AuthorizationResponse authorizationResponse
) {

}
