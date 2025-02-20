package nextstep.security.authentication;

import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public class OAuth2ClientProperties {

    private Map<String, Registration> registrations;
    private Map<String, Provider> providers;

    public Map<String, Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(Map<String, Registration> registrations) {
        this.registrations = registrations;
    }

    public Map<String, Provider> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, Provider> providers) {
        this.providers = providers;
    }

    public record Registration(
            String provider,
            String clientId,
            String clientSecret,
            String redirectUri,
            String authorizationGrantType,
            List<String> scope
    ) {
    }

    public record Provider(
            String authorizationUri,
            String tokenUri,
            String userInfoUri,
            String accessTokenUri
    ) {
    }
}
