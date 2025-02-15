package nextstep.oauth2.endpoint.dto;

import nextstep.oauth2.registration.ClientRegistration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static nextstep.oauth2.web.OAuth2ParameterNames.CLIENT_ID;
import static nextstep.oauth2.web.OAuth2ParameterNames.CLIENT_SECRET;
import static nextstep.oauth2.web.OAuth2ParameterNames.CODE;
import static nextstep.oauth2.web.OAuth2ParameterNames.GRANT_TYPE;
import static nextstep.oauth2.web.OAuth2ParameterNames.REDIRECT_URI;
import static nextstep.oauth2.web.OAuth2ParameterNames.STATE;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;

public record OAuth2AuthorizationCodeGrantRequest(
        ClientRegistration clientRegistration,
        OAuth2AuthorizationExchange authorizationExchange
) {
    public RequestEntity<MultiValueMap<String, String>> requestEntity() {
        return new RequestEntity<>(requestBody(), headers(), POST, tokenUri());
    }

    private MultiValueMap<String, String> requestBody() {
        final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(GRANT_TYPE, "authorization_code");
        body.add(CODE, authorizationExchange.authorizationResponse().code());
        body.add(STATE, authorizationExchange.authorizationRequest().state());
        body.add(REDIRECT_URI, authorizationExchange.authorizationRequest().redirectUri());
        body.add(CLIENT_ID, clientRegistration.clientId());
        body.add(CLIENT_SECRET, clientRegistration.clientSecret());
        return body;
    }

    private HttpHeaders headers() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, "application/x-www-form-urlencoded");
        return headers;
    }

    private URI tokenUri() {
        return UriComponentsBuilder.fromUriString(
                clientRegistration.providerDetails().tokenUri()
        ).build().toUri();
    }
}
