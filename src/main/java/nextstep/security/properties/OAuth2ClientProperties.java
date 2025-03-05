package nextstep.security.properties;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "security.oauth2")
public class OAuth2ClientProperties {
    private Map<String, Registration> registration = new HashMap<>();

    public Map<String, Registration> getRegistrations() {
        return registration;
    }

    @PostConstruct
    public void init() {
        registration.forEach((key, value) -> {
                    value.setRegistrationId(key);
                }
        );
    }

    @Nullable
    public Registration getRegistration(final String key) {
        return registration.get(key);
    }

    public void setRegistration(final Map<String, Registration> registration) {
        this.registration = registration;
    }

}
