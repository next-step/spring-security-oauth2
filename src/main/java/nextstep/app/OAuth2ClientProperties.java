package nextstep.app;

import nextstep.security.oauth2.registration.OAuth2ClientProviderProperties;
import nextstep.security.oauth2.registration.OAuth2ClientRegistrationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "oauth2.client")
public class OAuth2ClientProperties {
    private final Map<String, OAuth2ClientRegistrationProperties> registration;
    private final Map<String, OAuth2ClientProviderProperties> provider;

    public OAuth2ClientProperties(Map<String, OAuth2ClientRegistrationProperties> registration, Map<String, OAuth2ClientProviderProperties> provider) {
        this.registration = registration;
        this.provider = provider;
    }

    public OAuth2ClientRegistrationProperties getOauth2Registration(String providerName) {
        return registration.get(providerName);
    }

    public OAuth2ClientProviderProperties getOauth2Provider(String providerName) {
        return provider.get(providerName);
    }

    public Set<String> getRegistrations() {
        return registration.keySet();
    }
}
