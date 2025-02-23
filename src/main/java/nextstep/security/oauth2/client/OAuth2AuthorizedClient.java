package nextstep.security.oauth2.client;

import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.core.OAuth2AccessToken;

public record OAuth2AuthorizedClient(ClientRegistration clientRegistration, String principalName,
                                     OAuth2AccessToken accessToken) {
}
