package nextstep.app;

import jakarta.annotation.Nullable;
import nextstep.security.oauth2.ClientRegistration;
import nextstep.security.properties.ClientRegistrationRepository;
import nextstep.security.properties.OAuth2ClientProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InMemoryClientRegistrationRepository implements ClientRegistrationRepository {

    private final Map<String, ClientRegistration> registrations;

    public InMemoryClientRegistrationRepository(final OAuth2ClientProperties oAuth2ClientProperties) {
        this.registrations = ClientRegistration.from(oAuth2ClientProperties);
    }

    @Override
    @Nullable
    public ClientRegistration findRegistrationById(final String id) {
        return registrations.get(id);
    }

}
