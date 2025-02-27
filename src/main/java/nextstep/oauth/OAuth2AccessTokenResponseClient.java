package nextstep.oauth;

import nextstep.security.authentication.AuthenticationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public class OAuth2AccessTokenResponseClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken(ClientRegistration clientRegistration
            , OAuth2AuthorizationResponse oAuth2AuthorizationResponse) {

        MultiValueMap<String, String> body = extractBody(clientRegistration, oAuth2AuthorizationResponse);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        String tokenUri = clientRegistration.getTokenUri();
        URI uri = UriComponentsBuilder.fromUriString(tokenUri).build().toUri();

        RequestEntity<MultiValueMap<String, String>> entity = new RequestEntity<>(body, headers, HttpMethod.POST, uri);
        try {
            ResponseEntity<Map> results = this.restTemplate.exchange(entity, Map.class);
            return (String) results.getBody().get("access_token");
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }

    private static MultiValueMap<String, String> extractBody(ClientRegistration clientRegistration
            , OAuth2AuthorizationResponse oAuth2AuthorizationResponse) {
        String clientId = clientRegistration.getClientId();
        String clientSecret = clientRegistration.getClientSecret();
        String redirectUri = clientRegistration.getRedirectUri();
        String authorizationCode = oAuth2AuthorizationResponse.getCode();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authorizationCode);
        body.add("redirect_uri", redirectUri);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        return body;
    }
}
