package nextstep.security.oauth2.client;

import nextstep.security.oauth2.exception.ClientRegistrationNotFoundException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "oauth2.client")
public class ClientRegistrations {
    private final List<ClientRegistration> registration;

    public ClientRegistrations(List<ClientRegistration> registration) {
        this.registration = registration;
    }

    public List<ClientRegistration> getRegistration() {
        return registration;
    }

    public ClientRegistration getMatchingRegistration(String registrationId) {
        for (ClientRegistration clientRegistration : registration) {
            if (clientRegistration.getRegistrationId().equals(registrationId)) {
                return clientRegistration;
            }
        }

        throw new ClientRegistrationNotFoundException("Unknown registrationId: " + registrationId);
    }
}
