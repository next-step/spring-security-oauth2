package nextstep.oauth2.endpoint;

import nextstep.oauth2.authentication.OAuth2AccessToken;
import nextstep.oauth2.endpoint.dto.OAuth2AccessTokenResponse;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static nextstep.oauth2.web.OAuth2ParameterNames.ACCESS_TOKEN;

public class OAuth2AccessTokenResponseClient {
    private final RestTemplate rest = new RestTemplate();

    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        final String accessToken = (String) rest.exchange(
                grantRequest.requestEntity(), Map.class
        ).getBody().get(ACCESS_TOKEN);
        return new OAuth2AccessTokenResponse(new OAuth2AccessToken(accessToken));
    }
}
