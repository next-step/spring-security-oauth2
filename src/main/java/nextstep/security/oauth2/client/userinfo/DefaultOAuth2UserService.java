package nextstep.security.oauth2.client.userinfo;

import nextstep.security.authentication.AuthenticationException;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.core.user.OAuth2User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

public class DefaultOAuth2UserService implements OAuth2UserService {

    private final Logger logger = LoggerFactory.getLogger(DefaultOAuth2UserService.class);
    private final RestOperations restOperations = new RestTemplate();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        ClientRegistration.ProviderDetails provider = userRequest.registration().providerDetails();

        RequestEntity<?> request = RequestEntity
                .get(URI.create(provider.userInfoUri()))
                .headers(headers -> headers.setBearerAuth(userRequest.accessToken().value()))
                .build();

        logger.info("request: {}", request);
        ResponseEntity<Map<String, Object>> response = this.restOperations.exchange(
                request,
                new ParameterizedTypeReference<>() {}
        );
        logger.info("response: {}", response);

        Map<String, Object> userInfo = Objects.requireNonNullElseGet(response.getBody(), () -> {
            throw new AuthenticationException("Failed to get user info");
        });

        String usernameAttributeName = provider.userNameAttribute();
        Object username = Objects.requireNonNullElseGet(userInfo.get(usernameAttributeName), () -> {
            throw new AuthenticationException("Failed to get user identifier(=%s)".formatted(usernameAttributeName));
        });

        return username::toString;
    }
}
