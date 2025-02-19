package nextstep.security.authentication;


import static nextstep.security.authentication.GoogleLoginRedirectFilter.REDIRECT_URI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GoogleGetAccessTokenClient {
    private static final String GET_ACCESS_TOKEN_URI = "http://localhost:8089/oauth2.googleapis.com/token";
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HashMap<String, String> bodyParam = new HashMap<>();
        bodyParam.put("client_id", "349246449409-h7hgk8kms3k8d7tgal8nesbh24h34t0d.apps.googleusercontent.com");
        bodyParam.put("client_secret", "secret");
        bodyParam.put("code", code);
        bodyParam.put("redirect_uri", REDIRECT_URI);
        bodyParam.put("grant_type", "authorization_code");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(bodyParam, headers);

        ResponseEntity<Map> response = restTemplate.exchange(GET_ACCESS_TOKEN_URI, HttpMethod.POST, request,
                Map.class);

        return response.getBody().get("access_token").toString();
    }
}
