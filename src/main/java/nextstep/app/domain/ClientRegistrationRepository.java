package nextstep.app.domain;

import java.util.Optional;

public interface ClientRegistrationRepository {
    Optional<ClientRegistration> findByRegistrationId(String registrationId);
}
