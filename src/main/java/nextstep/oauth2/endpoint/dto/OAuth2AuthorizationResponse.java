package nextstep.oauth2.endpoint.dto;

public record OAuth2AuthorizationResponse(
        String redirectUri,
        String code,
        String state
) {}
