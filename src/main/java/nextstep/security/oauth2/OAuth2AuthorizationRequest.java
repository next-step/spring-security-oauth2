package nextstep.security.oauth2;

import nextstep.security.oauth2.registration.ClientRegistration;
import org.springframework.web.util.UriComponentsBuilder;

public class OAuth2AuthorizationRequest {
    private final String authorizationUri;
    private final String clientId;
    private final String redirectUri;
    private final String scopes;
    private final String authorizationRequestUri;
    private final String responseType;
    private final String oauth2AuthorizationRedirectURl;
    private final String registrationId;

    private OAuth2AuthorizationRequest(String authorizationUri, String clientId, String redirectUri, String scopes, String authorizationRequestUri, String responseType, String registrationId) {
        this.authorizationUri = authorizationUri;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.scopes = scopes;
        this.authorizationRequestUri = authorizationRequestUri;
        this.responseType = responseType;
        this.oauth2AuthorizationRedirectURl = generateOauthRedirectURL(clientId, redirectUri, scopes, authorizationRequestUri, responseType);
        this.registrationId = registrationId;
    }

    public static OAuth2AuthorizationRequest from(ClientRegistration clientRegistration) {
        return new OAuth2AuthorizationRequest(
                clientRegistration.getAuthorizationUri(),
                clientRegistration.getClientId(),
                clientRegistration.getRedirectUri(),
                clientRegistration.getScope(),
                clientRegistration.getAuthorizationUri(),
                clientRegistration.getResponseType(),
                clientRegistration.getRegistrationId()
        );

    }

    private String generateOauthRedirectURL(String clientId, String redirectUri, String scopes, String authorizationRequestUri, String responseType) {
        return UriComponentsBuilder.fromHttpUrl(authorizationRequestUri)
                .queryParam("client_id", clientId)
                .queryParam("response_type", responseType)
                .queryParam("scope", scopes)
                .queryParam("redirect_uri", redirectUri)
                .build().toString();
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScopes() {
        return scopes;
    }

    public String getAuthorizationRequestUri() {
        return authorizationRequestUri;
    }

    public String getOauth2AuthorizationRedirectURl() {
        return oauth2AuthorizationRedirectURl;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getRegistrationId() {
        return registrationId;
    }
}
