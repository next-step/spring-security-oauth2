package nextstep.oauth2.endpoint.dto;

public record OAuth2AuthorizationExchange(
        OAuth2AuthorizationRequest authorizationRequest,
        OAuth2AuthorizationResponse authorizationResponse
) {}
