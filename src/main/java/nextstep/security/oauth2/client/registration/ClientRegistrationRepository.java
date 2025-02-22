package nextstep.security.oauth2.client.registration;

import java.util.Map;

public interface ClientRegistrationRepository {

    ClientRegistration findByRegistrationId(String registrationId);

    class InMemoryClientRegistrationRepository implements ClientRegistrationRepository {
        private final Map<String, ClientRegistration> registrations;

        public InMemoryClientRegistrationRepository(Map<String, ClientRegistration> registrations) {
            this.registrations = registrations;
        }

        @Override
        public ClientRegistration findByRegistrationId(String registrationId) {
            if (registrationId == null || registrationId.isBlank()) {
                throw new IllegalArgumentException("Registration ID must not be null or empty");
            }
            return registrations.get(registrationId);
        }

    }
}


