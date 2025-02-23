package nextstep.fixture;

import nextstep.security.oauth2.registration.ClientRegistration;

public class TestClientFixture {
    public static ClientRegistration create(String registrationId) {
        return ClientRegistration.builder(registrationId)
                .clientId(registrationId + "-client-id")
                .responseType("code")
                .redirectUri("http://localhost:8080/login/oauth2/code" + registrationId)
                .scope("profile email")
                .clientSecret(registrationId + "-secret")
                .tokenUri("https://www." + registrationId + ".com/oauth2/v3/token")
                .userInfoUri("https://www." + registrationId + "./oauth2/v3/userinfo")
                .authorizationUri("https://accounts." + registrationId + ".com/o/oauth2/v2/auth")
                .build();
    }
}
