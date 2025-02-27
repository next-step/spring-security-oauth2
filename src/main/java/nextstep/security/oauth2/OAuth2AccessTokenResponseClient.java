package nextstep.security.oauth2;


import java.net.URI;
import java.util.Map;
import nextstep.security.oauth2.client.OAuth2AuthorizationCodeGrantRequest;
import nextstep.security.oauth2.client.authentication.OAuth2AccessTokenResponse;
import nextstep.security.oauth2.core.OAuth2AccessToken;
import nextstep.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class OAuth2AccessTokenResponseClient {

    private final RestTemplate restOperations = new RestTemplate();

    private static MultiValueMap<String, String> getStringStringMultiValueMap(
            OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest, String clientId) {
        String clientSecret = authorizationCodeGrantRequest.clientRegistration().clientSecret();
        String redirectUri = authorizationCodeGrantRequest.authorizationExchange().authorizationRequest()
                .redirectUri();
        String authorizationCode = authorizationCodeGrantRequest.authorizationExchange().authorizationResponse().code();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authorizationCode);
        body.add("redirect_uri", redirectUri);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        return body;
    }

    public OAuth2AccessTokenResponse getTokenResponse(
            OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {

        RequestEntity<?> request = convertRequestEntity(authorizationCodeGrantRequest);
        OAuth2AccessTokenResponse tokenResponse = getResponse(request);
        Assert.notNull(tokenResponse,
                "The authorization server responded to this Authorization Code grant request with an empty body; as such, it cannot be materialized into an OAuth2AccessTokenResponse instance. Please check the HTTP response code in your server logs for more details.");
        return tokenResponse;
    }

    private RequestEntity<?> convertRequestEntity(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        String tokenUri = authorizationCodeGrantRequest.clientRegistration().providerDetails().tokenUri();
        String clientId = authorizationCodeGrantRequest.clientRegistration().clientId();
        MultiValueMap<String, String> body = getStringStringMultiValueMap(
                authorizationCodeGrantRequest, clientId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        URI uri = UriComponentsBuilder.fromUriString(tokenUri).build().toUri();

        return new RequestEntity<>(body, headers, HttpMethod.POST, uri);
    }

    private OAuth2AccessTokenResponse getResponse(RequestEntity<?> request) {
        try {
            ResponseEntity<Map> results = this.restOperations.exchange(request, Map.class);
            String accessToken = (String) results.getBody().get("access_token");
            return new OAuth2AccessTokenResponse(new OAuth2AccessToken(accessToken));
        } catch (Exception e) {
            throw new OAuth2AuthenticationException();
        }
    }
}

