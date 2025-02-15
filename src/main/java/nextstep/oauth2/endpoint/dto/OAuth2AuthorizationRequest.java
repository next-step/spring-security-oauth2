package nextstep.oauth2.endpoint.dto;

import java.util.Set;

public record OAuth2AuthorizationRequest(
        String authorizationUri,
        String clientId,
        String redirectUri,
        Set<String> scopes,
        String state,
        String authorizationRequestUri
) {}
