package nextstep.security.authentication;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


public class OAuth2AuthenticationProvider implements AuthenticationProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2LoginAuthenticationToken oAuth2LoginAuthenticationToken = (OAuth2LoginAuthenticationToken) authentication;
        AccessTokenResponseDTO accessTokenResponse = getAccessToken(oAuth2LoginAuthenticationToken);
        UserProfile userProfile = getUserProfile(accessTokenResponse, oAuth2LoginAuthenticationToken.getClientRegistration());
        return OAuth2LoginAuthenticationToken.authenticated(userProfile.getEmail(), oAuth2LoginAuthenticationToken.getClientRegistration(), accessTokenResponse.getAccess_token());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private AccessTokenResponseDTO getAccessToken(OAuth2LoginAuthenticationToken oAuth2LoginAuthenticationToken) {
        ClientRegistration clientRegistration = oAuth2LoginAuthenticationToken.getClientRegistration();
        String code = oAuth2LoginAuthenticationToken.getCode();
        String tokenUri = clientRegistration.getTokenUri();
        MultiValueMap<String, String> paramsForToken = clientRegistration.getParamsForToken(code);
        return restTemplate.postForObject(tokenUri, paramsForToken, AccessTokenResponseDTO.class);
    }

    private UserProfile getUserProfile(AccessTokenResponseDTO accessTokenResponse, ClientRegistration clientRegistration) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessTokenResponse.getToken());
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        String userInfoUri = clientRegistration.getUserInfoUri();
        ResponseEntity<UserProfile> userProfileDTOResponseEntity = restTemplate.exchange(userInfoUri, HttpMethod.GET, httpEntity, UserProfile.class);
        return userProfileDTOResponseEntity.getBody();
    }

}
