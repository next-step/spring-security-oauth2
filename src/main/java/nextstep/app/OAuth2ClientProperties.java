package nextstep.app;

import nextstep.security.oauth2.Oauth2ClientRegistrationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "oauth2.client")
public class OAuth2ClientProperties {
    private final Map<String, Oauth2ClientRegistrationProperties> registration;

    public OAuth2ClientProperties(Map<String, Oauth2ClientRegistrationProperties> registration) {
        this.registration = registration;
    }

    public Oauth2ClientRegistrationProperties getOauth2Properties(String clientId) {
        return registration.get(clientId);
    }
}
