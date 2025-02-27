package nextstep.security.oauth2.client;

import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.core.OAuth2AuthorizationExchange;

public record OAuth2AuthorizationCodeGrantRequest(ClientRegistration clientRegistration,
                                                  OAuth2AuthorizationExchange authorizationExchange) {
}
