package nextstep.security.oauth2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestClient;

public class OAuth2ProviderClient {
    private final OAuth2ClientRegistrationProperties properties;
    private final OAuth2ClientProviderProperties providerProperties;
    private final RestClient.Builder clientBuilder;


    public OAuth2ProviderClient(OAuth2ClientRegistrationProperties properties, OAuth2ClientProviderProperties providerProperties) {
        this.properties = properties;
        this.providerProperties = providerProperties;
        this.clientBuilder = RestClient.builder().messageConverters((it) -> it.add(new FormHttpMessageConverter()));
    }

    public OAuth2AccessToken accessTokenRequest(String code) {
        return clientBuilder.build()
                .post()
                .uri(providerProperties.getTokenUri())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(OAuth2AccessTokenRequest.of(properties, code))
                .retrieve()
                .body(OAuth2AccessToken.class);
    }

    public OAuth2UserInfo getUserInfo(OAuth2AccessToken accessToken) {
        return clientBuilder.build()
                .get()
                .uri(providerProperties.getUserInfoUri())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .retrieve()
                .body(OAuth2UserInfo.class);
    }
}
