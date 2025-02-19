package nextstep.app;

import nextstep.oauth2.OAuth2ClientProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest
public class OAuth2ClientPropertiesTest {
    @Autowired
    private OAuth2ClientProperties oAuth2ClientProperties;

    @Test
    public void loadTest() {
        final Map<String, OAuth2ClientProperties.Provider> provider = oAuth2ClientProperties.getProvider();
        System.out.println("provider = " + provider);

        System.out.println("====================================");
        final Map<String, OAuth2ClientProperties.Registration> registration = oAuth2ClientProperties.getRegistration();
        System.out.println("registration = " + registration);

    }
}
