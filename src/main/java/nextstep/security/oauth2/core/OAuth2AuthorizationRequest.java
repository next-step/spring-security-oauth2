package nextstep.security.oauth2.core;

import java.util.Set;

public record OAuth2AuthorizationRequest(
        String authorizationUri,
        String clientId,
        String redirectUri,
        Set<String> scope,
        String authorizationRequestUri
) {

}
