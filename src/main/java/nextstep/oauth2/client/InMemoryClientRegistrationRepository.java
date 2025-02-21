package nextstep.oauth2.client;

import java.util.Map;

public class InMemoryClientRegistrationRepository implements ClientRegistrationRepository {
    private final Map<String, ClientRegistration> registrations;

    public InMemoryClientRegistrationRepository(final Map<String, ClientRegistration> registrations) {
        this.registrations = registrations;
    }

    @Override
    public ClientRegistration findByRegistrationId(final String registrationId) {
        return this.registrations.get(registrationId);
    }
}
