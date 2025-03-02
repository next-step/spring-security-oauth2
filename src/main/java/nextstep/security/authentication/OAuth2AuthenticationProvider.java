package nextstep.security.authentication;

import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class OAuth2AuthenticationProvider implements AuthenticationProvider {

    private final RestTemplate restTemplate = new RestTemplate();
    private final OAuth2UserService userService;

    public OAuth2AuthenticationProvider(OAuth2UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2LoginAuthenticationToken oAuth2LoginAuthenticationToken = (OAuth2LoginAuthenticationToken) authentication;
        OAuth2AccessToken accessToken = getAccessToken(oAuth2LoginAuthenticationToken);

        OAuth2UserRequest userRequest = new OAuth2UserRequest(accessToken, oAuth2LoginAuthenticationToken.getClientRegistration());
        OAuth2User oAuth2User = userService.loadUser(userRequest);

        return OAuth2LoginAuthenticationToken.authenticated(oAuth2User.getEmail(), oAuth2LoginAuthenticationToken.getClientRegistration(), accessToken.getAccess_token());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private OAuth2AccessToken getAccessToken(OAuth2LoginAuthenticationToken oAuth2LoginAuthenticationToken) {
        ClientRegistration clientRegistration = oAuth2LoginAuthenticationToken.getClientRegistration();
        String code = oAuth2LoginAuthenticationToken.getCode();
        String tokenUri = clientRegistration.getTokenUri();
        MultiValueMap<String, String> paramsForToken = clientRegistration.getParamsForToken(code);
        return restTemplate.postForObject(tokenUri, paramsForToken, OAuth2AccessToken.class);
    }
}
