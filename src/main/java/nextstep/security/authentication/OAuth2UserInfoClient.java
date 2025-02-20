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

    public OAuth2UserInfo getUserInfo(String accessToken, Provider provider) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(httpHeaders);
        ResponseEntity<Map> response = restTemplate.exchange(provider.userInfoUri(), HttpMethod.GET,
                request, Map.class);

        Map<String, Object> attributes = response.getBody();

        return OAuth2UserInfoFactory.getOAuth2UserInfo(provider.name(), attributes);
    }
}
