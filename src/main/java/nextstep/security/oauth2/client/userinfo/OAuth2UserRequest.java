package nextstep.security.oauth2.client.userinfo;

import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.core.OAuth2AccessToken;

public record OAuth2UserRequest(ClientRegistration clientRegistration, OAuth2AccessToken accessToken) {
}
