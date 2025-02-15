package nextstep.oauth2.endpoint.dto;

import nextstep.oauth2.registration.ClientRegistration;

public record OAuth2AuthorizationCodeGrantRequest(
        ClientRegistration clientRegistration,
        OAuth2AuthorizationExchange authorizationExchange
) {}
