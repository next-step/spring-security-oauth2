package nextstep.oauth2.client.userinfo;

import nextstep.oauth2.exception.OAuth2UserInfoEndPointNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class DefaultOAuth2UserService implements OAuth2UserService {

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    private final RestClient restClient = RestClient.create();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        Assert.notNull(userRequest, "userRequest cannot be null");
        String userNameAttributeName = getUserNameAttributeName(userRequest);
        final Map<String, Object> attributes = fetchAttributes(userRequest);
        return new DefaultOAuth2User(attributes, userNameAttributeName);
    }

    private String getUserNameAttributeName(OAuth2UserRequest userRequest) {
        if (userRequest.isNotUserInfoUri()) {
            throw new OAuth2UserInfoEndPointNotFoundException();
        }

        if (userRequest.isNotUserNameAttributeName()) {
            throw new OAuth2UserInfoEndPointNotFoundException();
        }

        return userRequest.userNameAttributeName();
    }

    public Map<String, Object> fetchAttributes(OAuth2UserRequest userRequest) {
        final String userInfoUri = userRequest.userInfoUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userRequest.accessToken().value());
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        return restClient.get()
                .uri(userInfoUri)
                .headers(h -> h.addAll(headers))
                .retrieve()
                .body(PARAMETERIZED_RESPONSE_TYPE);
    }
}
