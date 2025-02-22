package nextstep.security.oauth2.registration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ClientRegistrationTest {
    private final String clientId = "clientId";
    private final String clientSecret = "clientSecret";
    private final String responseType = "responseType";
    private final String authorizationUri = "authorizationUri";
    private final String tokenUri = "tokenUri";
    private final String redirectUri = "redirectUri";
    private final String userInfoUri = "scope";


    @Test
    @DisplayName("ClientRegistration 가 정상적으로 생성된다")
    void builder() {
        String registrationId = "registrationId";


        final ClientRegistration clientRegistration = ClientRegistration.builder(registrationId)
                .clientId(clientId)
                .responseType("code")
                .redirectUri(redirectUri)
                .scope("profile email")
                .clientSecret(clientSecret)
                .tokenUri(tokenUri)
                .userInfoUri(userInfoUri)
                .responseType(responseType)
                .authorizationUri(authorizationUri)
                .build();


        assertSoftly(it -> {
            it.assertThat(clientRegistration.getRegistrationId()).isEqualTo(registrationId);
            it.assertThat(clientRegistration.getClientId()).isEqualTo(clientId);
            it.assertThat(clientRegistration.getClientSecret()).isEqualTo(clientSecret);
            it.assertThat(clientRegistration.getTokenUri()).isEqualTo(tokenUri);
            it.assertThat(clientRegistration.getUserInfoUri()).isEqualTo(userInfoUri);
        });
    }
}
