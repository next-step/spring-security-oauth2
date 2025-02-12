package nextstep.app;

import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.registration.OAuth2ClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(OAuth2ClientProperties.class)
@Configuration
public class OAuth2Config {
    private final ClientRegistrationRepository registrationRepository;

    public OAuth2Config(OAuth2ClientProperties oauth2ClientProperties) {
        this.registrationRepository = oauth2ClientProperties.createClientRegistrationDao();
    }
}
