package nextstep.app.support;

import nextstep.app.testsupport.BaseIntegrationTestSupport;
import nextstep.oauth2.OAuth2ClientProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityOAuth2PropertiesTest extends BaseIntegrationTestSupport {

    @Autowired
    OAuth2ClientProperties sut;

    @Test
    void load_oauth2_client_infos() {
        Map<String, OAuth2ClientProperties.Registration> registration = sut.getRegistration();
        Map<String, OAuth2ClientProperties.Provider> provider = sut.getProvider();

        OAuth2ClientProperties.Registration githubRegistration = registration.get("github");
        assertThat(githubRegistration).isNotNull();
        assertThat(githubRegistration.provider()).isEqualTo("github");
        assertThat(githubRegistration.clientId()).isEqualTo("test-github-client-id");
        assertThat(githubRegistration.clientSecret()).isEqualTo("test-github-client-secret");

        OAuth2ClientProperties.Provider githubProvider = provider.get("github");
        assertThat(githubProvider).isNotNull();
        assertThat(githubProvider.authorizationUri()).isEqualTo("http://localhost:8089/login/oauth/authorize");
    }
}
