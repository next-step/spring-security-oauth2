package nextstep.security.oauth2.client.registration;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryClientRegistrationRepository implements ClientRegistrationRepository {

    private final Map<String, ClientRegistration> registrations; // <RegistrationId, ClientRegistration>

    public InMemoryClientRegistrationRepository(List<ClientRegistration> registrations) {
        this.registrations = registrations.stream()
                .collect(Collectors.toMap(
                        ClientRegistration::registrationId,
                        registration -> registration
                ));
    }

    @Override
    @Nullable
    public ClientRegistration findByRegistrationId(String registrationId) {
        Assert.hasText(registrationId, "registrationId cannot be empty");
        return registrations.get(registrationId);
    }
}
