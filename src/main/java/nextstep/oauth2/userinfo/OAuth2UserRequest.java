package nextstep.oauth2.userinfo;

import nextstep.oauth2.authentication.OAuth2AccessToken;
import nextstep.oauth2.exception.OAuth2AuthenticationException;
import nextstep.oauth2.registration.ClientRegistration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

public record OAuth2UserRequest(
        ClientRegistration clientRegistration,
        OAuth2AccessToken accessToken
) {
    public RequestEntity<?> requestEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.token());
        headers.add(HttpHeaders.ACCEPT, "application/json");

        final URI uri = UriComponentsBuilder.fromUriString(
                clientRegistration.providerDetails().userInfoEndpoint().uri()
        ).build().toUri();

        return new RequestEntity<>(headers, GET, uri);
    }

    public Map<String, Object> customAttributes(Map<String, Object> body) {
        final Map<String, Object> customAttributes = new HashMap<>(body);

        // TODO: Custom Attribute 를 추가하거나 변형하는 로직 작성

        return customAttributes;
    }

    public String userNameAttributeName() {
        if (!clientRegistration.providerDetails().userInfoEndpoint().hasText()) {
            throw new OAuth2AuthenticationException();
        }
        return clientRegistration.providerDetails()
                .userInfoEndpoint().userNameAttributeName();
    }
}
