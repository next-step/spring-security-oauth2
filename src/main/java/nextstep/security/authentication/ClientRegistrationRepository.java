package nextstep.security.authentication;

import java.util.Map;

public class ClientRegistrationRepository {

    private final Map<String, ClientRegistration> clientRegistrations;

    public ClientRegistrationRepository(Map<String, ClientRegistration> clientRegistrations) {
        this.clientRegistrations = clientRegistrations;
    }

    public ClientRegistration findByRegistrationId(String registrationId) {
        return clientRegistrations.get(registrationId);
    }
}
