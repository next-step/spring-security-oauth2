package nextstep.oauth2.web;

import nextstep.oauth2.authentication.OAuth2AccessToken;
import nextstep.oauth2.registration.ClientRegistration;

public record OAuth2AuthorizedClient(
        ClientRegistration clientRegistration,
        String principalName,
        OAuth2AccessToken accessToken
) {}
