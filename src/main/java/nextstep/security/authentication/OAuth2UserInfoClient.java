package nextstep.security.authentication;

import java.util.Map;
import nextstep.security.authentication.OAuth2ClientProperties.Provider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class OAuth2UserInfoClient {

  private final RestTemplate restTemplate = new RestTemplate();

  public Map<String, String> getUserInfo(String accessToken, Provider provider) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + accessToken);

    HttpEntity<Void> request = new HttpEntity<>(httpHeaders);
    ResponseEntity<Map> response = restTemplate.exchange(provider.getUserInfoUri(), HttpMethod.GET,
        request, Map.class);

    return response.getBody();
  }
}
