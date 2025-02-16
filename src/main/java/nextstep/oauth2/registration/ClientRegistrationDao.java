package nextstep.oauth2.registration;

import nextstep.oauth2.exception.UnsupportedRegistrationIdException;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public final class ClientRegistrationDao implements ClientRegistrationRepository {
    private final Map<String, ClientRegistration> registrations;

    public ClientRegistrationDao(Map<String, ClientRegistration> registrations) {
        this.registrations = registrations;
    }

    public ClientRegistration findByRegistrationId(String registrationId) {
        final ClientRegistration registration = registrations.get(registrationId);
        if (registration == null) {
            throw new UnsupportedRegistrationIdException(registrationId);
        }
        return registration;
    }
}
