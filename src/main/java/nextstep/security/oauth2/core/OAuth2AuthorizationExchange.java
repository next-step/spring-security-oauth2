package nextstep.security.oauth2.core;

import org.springframework.util.Assert;

public record OAuth2AuthorizationExchange(
        OAuth2AuthorizationRequest authorizationRequest,
        OAuth2AuthorizationResponse authorizationResponse) {

    public OAuth2AuthorizationExchange {
        Assert.notNull(authorizationRequest, "authorizationRequest cannot be null");
        Assert.notNull(authorizationResponse, "authorizationResponse cannot be null");
    }
}
