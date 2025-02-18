package nextstep.security.oauth2.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryClientRegistrationRepository implements ClientRegistrationRepository {
    private final Map<String, ClientRegistration> registrations;

    public InMemoryClientRegistrationRepository(List<ClientRegistration> registrations) {
        Map<String, ClientRegistration> registrationMap = new HashMap<>();
        for (ClientRegistration registration : registrations) {
            registrationMap.put(registration.getRegistrationId(), registration);
        }

        this.registrations = registrationMap;
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return registrations.get(registrationId);
    }
}
