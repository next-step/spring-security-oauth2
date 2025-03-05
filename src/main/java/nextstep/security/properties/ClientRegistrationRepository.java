package nextstep.security.properties;

import jakarta.annotation.Nullable;
import nextstep.security.oauth2.ClientRegistration;

public interface ClientRegistrationRepository {
    @Nullable
    ClientRegistration findRegistrationById(final String id);
}
