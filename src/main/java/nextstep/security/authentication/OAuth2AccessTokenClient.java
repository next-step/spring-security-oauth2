package nextstep.security.authentication;

import static nextstep.security.authentication.OAuth2ClientProperties.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class OAuth2AccessTokenClient {

  private final RestTemplate restTemplate = new RestTemplate();

  public String getAccessToken(String code, Registration registration, Provider provider) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, String> params = new HashMap<>();
    params.put("code", code);
    params.put("client_id", registration.clientId());
    params.put("client_secret", registration.clientSecret());
    params.put("redirect_uri", registration.redirectUri());
    params.put("grant_type", "authorization_code");

    HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

    ResponseEntity<Map> response = restTemplate.exchange(
        provider.accessTokenUri(),
        HttpMethod.POST,
        request,
        Map.class
    );

    return response.getBody().get("access_token").toString();
  }
}
