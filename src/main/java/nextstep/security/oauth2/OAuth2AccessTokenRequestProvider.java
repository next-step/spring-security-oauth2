package nextstep.security.oauth2;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


public class OAuth2AccessTokenRequestProvider {
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken(final ClientRegistration registration, final String code) {
        Map<String, String> map = new HashMap<>();
        map.put(OAuth2Parameter.CLIENT_ID.getPath(), registration.getClientId());
        map.put(OAuth2Parameter.CLIENT_SECRET.getPath(), registration.getClientSecret());
        map.put(OAuth2Parameter.CODE.getPath(), code);
        map.put(OAuth2Parameter.GRANT_TYPE.getPath(), registration.getGrantType());
        map.put(OAuth2Parameter.REDIRECT_URL.getPath(), registration.getRedirectUri());
        var response = doPost(registration.getTokenUri(), map);
        return (String) response.get("access_token");
    }

    private Map<String, Object> doPost(final String uri, final Map<String, String> parameterMap) {
        try {
            return restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(parameterMap), new ParameterizedTypeReference<Map<String, Object>>() {
            }).getBody();
        } catch (Exception ex) {
            throw new IllegalStateException("oauth2 accessToken 정보를 가져오는데 실패했습니다." + ex.getMessage());
        }
    }


}
