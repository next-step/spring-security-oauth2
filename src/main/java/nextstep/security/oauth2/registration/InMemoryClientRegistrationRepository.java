package nextstep.security.oauth2.registration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryClientRegistrationRepository implements ClientRegistrationRepository {
    private final Map<String, ClientRegistration> registrations;

    public InMemoryClientRegistrationRepository(List<ClientRegistration> clientRegistrations) {
        this.registrations = createRegistrations(clientRegistrations);
    }

    private Map<String, ClientRegistration> createRegistrations(List<ClientRegistration> clientRegistrations) {
        Map<String, ClientRegistration> map = new ConcurrentHashMap<>();
        for (ClientRegistration clientRegistration : clientRegistrations) {
            map.put(clientRegistration.getRegistrationId(), clientRegistration);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return registrations.get(registrationId);
    }
}
