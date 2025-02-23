package nextstep.security.oauth2;

import nextstep.security.oauth2.registration.ClientRegistration;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class OAuth2AuthorizationRequest {
    private final UriComponents oauth2AuthorizationRedirectURl;

    private OAuth2AuthorizationRequest(UriComponents oauth2AuthorizationRedirectURl) {
        this.oauth2AuthorizationRedirectURl = oauth2AuthorizationRedirectURl;
    }

    public static OAuth2AuthorizationRequest from(ClientRegistration clientRegistration) {
        return new OAuth2AuthorizationRequest(UriComponentsBuilder.fromHttpUrl(clientRegistration.getAuthorizationUri())
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("response_type", clientRegistration.getResponseType())
                .queryParam("scope", clientRegistration.getScope())
                .queryParam("redirect_uri", clientRegistration.getRedirectUri())
                .build()
        );
    }

    public String getOauth2AuthorizationRedirectURl() {
        return oauth2AuthorizationRedirectURl.toString();
    }
}
