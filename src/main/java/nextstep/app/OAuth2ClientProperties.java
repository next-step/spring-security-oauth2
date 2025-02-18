package nextstep.app;

import nextstep.security.oauth2.Oauth2ClientProviderProperties;
import nextstep.security.oauth2.Oauth2ClientRegistrationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "oauth2.client")
public class OAuth2ClientProperties {
    private final Map<String, Oauth2ClientRegistrationProperties> registration;
    private final Map<String, Oauth2ClientProviderProperties> provider;

    public OAuth2ClientProperties(Map<String, Oauth2ClientRegistrationProperties> registration, Map<String, Oauth2ClientProviderProperties> provider) {
        this.registration = registration;
        this.provider = provider;
    }

    public Oauth2ClientRegistrationProperties getOauth2Registration(String providerName) {
        return registration.get(providerName);
    }

    public Oauth2ClientProviderProperties getOauth2Provider(String providerName) {
        return provider.get(providerName);
    }
}
