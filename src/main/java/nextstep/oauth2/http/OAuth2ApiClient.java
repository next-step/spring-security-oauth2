package nextstep.oauth2.http;

import nextstep.oauth2.registration.ClientRegistration;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class OAuth2ApiClient {
    private final RestClient restClient = RestClient.create();

    public String sendTokenRequest(final ClientRegistration clientRegistration, final String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientRegistration.getClientId());
        body.add("client_secret", clientRegistration.getClientSecret());
        body.add("code", code);
        body.add("redirect_uri", clientRegistration.getRedirectUri());
        body.add("grant_type", "authorization_code");

        final Map<String, String> response = restClient.post()
                .uri(clientRegistration.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        return response.get("access_token");
    }
}
