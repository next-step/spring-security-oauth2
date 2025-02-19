package nextstep.security.authentication;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GoogleGetUserInfoClient {
    private static final String GET_USER_INFO_URI = "http://localhost:8089/oauth2/v1/userinfo";
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> getUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(httpHeaders);
        ResponseEntity<Map> response = restTemplate.exchange(GET_USER_INFO_URI, HttpMethod.GET, request, Map.class);
        return response.getBody();
    }
}
