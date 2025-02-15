package nextstep.oauth2.endpoint.dto;

public record OAuth2AuthorizationResponse(
        String code,
        String redirectUri,
        String state
) {}
