package nextstep.security.oauth2;

import nextstep.security.oauth2.registration.ClientRegistration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestClient;

public class OAuth2ProviderClient {
    private final ClientRegistration clientRegistration;
    private final RestClient.Builder clientBuilder;

    public OAuth2ProviderClient(ClientRegistration clientRegistration) {
        this.clientRegistration = clientRegistration;
        this.clientBuilder = RestClient.builder().messageConverters((it) -> it.add(new FormHttpMessageConverter()));
    }

    public OAuth2AccessToken accessTokenRequest(OAuth2AccessTokenRequest oAuth2AccessTokenRequest) {
        return clientBuilder.build()
                .post()
                .uri(clientRegistration.getTokenUri())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(oAuth2AccessTokenRequest)
                .retrieve()
                .body(OAuth2AccessToken.class);
    }

    public OAuth2UserInfo getUserInfo(OAuth2AccessToken accessToken) {
        return clientBuilder.build()
                .get()
                .uri(clientRegistration.getUserInfoUri())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .retrieve()
                .body(OAuth2UserInfo.class);
    }
}
