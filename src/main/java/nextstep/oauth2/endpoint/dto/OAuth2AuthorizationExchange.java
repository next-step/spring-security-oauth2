package nextstep.oauth2.endpoint.dto;

public record OAuth2AuthorizationExchange(
        OAuth2AuthorizationRequest authorizationRequest,
        OAuth2AuthorizationResponse authorizationResponse
) {
    public boolean isSameState() {
        return authorizationRequest.state().equals(
                authorizationResponse.state()
        );
    }
}
