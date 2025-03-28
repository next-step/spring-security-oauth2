package nextstep.security.oauth2.client.endpoint;

import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.core.OAuth2AccessToken;
import nextstep.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

public class OAuth2AccessTokenResponseClient {

    private final RestOperations restOperations = new RestTemplate();

    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest codeGrantRequest) {
        RequestEntity<?> requestEntity = convertToRequestEntity(codeGrantRequest);
        return getResponse(requestEntity);
    }

    private RequestEntity<MultiValueMap<String, String>> convertToRequestEntity(
            OAuth2AuthorizationCodeGrantRequest request
    ) {
        ClientRegistration registration = request.getRegistration();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.CODE, request.getAuthorizationExchange().authorizationResponse().code());
        params.add(OAuth2ParameterNames.CLIENT_ID, registration.clientId());
        params.add(OAuth2ParameterNames.CLIENT_SECRET, registration.clientSecret());
        params.add(OAuth2ParameterNames.REDIRECT_URI, registration.redirectUri());
        params.add(OAuth2ParameterNames.GRANT_TYPE, request.getAuthorizationGrantType());

        return RequestEntity
                .post(URI.create(registration.providerDetails().tokenUri()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params);
    }

    private OAuth2AccessTokenResponse getResponse(RequestEntity<?> requestEntity) {
        ResponseEntity<Map> results = restOperations.exchange(requestEntity, Map.class);
        Map responseBody = results.getBody();
        String accessTokenValue = (String) responseBody.get(OAuth2ParameterNames.ACCESS_TOKEN);
        return new OAuth2AccessTokenResponse(new OAuth2AccessToken(accessTokenValue));
    }
}
