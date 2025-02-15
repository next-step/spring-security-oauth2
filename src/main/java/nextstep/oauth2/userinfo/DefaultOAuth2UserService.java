package nextstep.oauth2.userinfo;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static java.util.Collections.emptySet;

public class DefaultOAuth2UserService implements OAuth2UserService {
    private final RestTemplate rest = new RestTemplate();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        final ResponseEntity<Map<String, Object>> response = rest.exchange(
                userRequest.requestEntity(),
                new ParameterizedTypeReference<>() {}
        );
        return new DefaultOAuth2User(
                emptySet(),
                userRequest.customAttributes(response.getBody()),
                userRequest.userNameAttributeName()
        );
    }
}
