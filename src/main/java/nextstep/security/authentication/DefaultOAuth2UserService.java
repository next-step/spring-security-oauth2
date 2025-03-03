package nextstep.security.authentication;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DefaultOAuth2UserService implements OAuth2UserService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        ClientRegistration clientRegistration = userRequest.getClientRegistration();

        ResponseEntity<UserProfile> userProfileResponseEntity = getResponse(clientRegistration, accessToken);
        return DefaultOAuth2User.from(userProfileResponseEntity.getBody());
    }

    private ResponseEntity<UserProfile> getResponse(ClientRegistration clientRegistration, OAuth2AccessToken accessToken) {
        return restTemplate.exchange(clientRegistration.getUserInfoUri(),
                HttpMethod.GET,
                new HttpEntity<>(getHttpHeaders(accessToken)),
                UserProfile.class);
    }

    private static HttpHeaders getHttpHeaders(OAuth2AccessToken accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken.getToken());
        return headers;
    }
}
