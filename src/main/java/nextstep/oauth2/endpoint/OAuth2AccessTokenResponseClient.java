package nextstep.oauth2.endpoint;

import nextstep.oauth2.authentication.OAuth2AccessToken;
import nextstep.oauth2.registration.ClientRegistration;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class OAuth2AccessTokenResponseClient {
    private final RestClient restClient = RestClient.create();

    public OAuth2AccessTokenResponse getTokenResponse(final OAuth2AuthorizationCodeGrantRequest oAuth2AuthorizationCodeGrantRequest) {
        final String token = fetchToken(oAuth2AuthorizationCodeGrantRequest);
        return createTokenResponse(token);
    }

    private String fetchToken(final OAuth2AuthorizationCodeGrantRequest oAuth2AuthorizationCodeGrantRequest) {
        final ClientRegistration clientRegistration = oAuth2AuthorizationCodeGrantRequest.getClientRegistration();
        final OAuth2AuthorizationExchange authorizationExchange = oAuth2AuthorizationCodeGrantRequest.getAuthorizationExchange();

        final String code = authorizationExchange.getAuthorizationResponse().getCode();
        final String redirectUri = authorizationExchange.getAuthorizationRequest().getRedirectUri();
        final String clientId = clientRegistration.getClientId();
        final String clientSecret = clientRegistration.getClientSecret();
        final String state = authorizationExchange.getAuthorizationRequest().getState();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("state", state);

        final Map<String, String> response = restClient.post()
                .uri(clientRegistration.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        return response.get("access_token");
    }

    private OAuth2AccessTokenResponse createTokenResponse(final String token) {
        return new OAuth2AccessTokenResponse(new OAuth2AccessToken(token));
    }
}
