package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;

import java.util.Map;

public class OAuth2ClientRepository {

    private final Map<String, ClientRegistration> clientRegistrations;

    public OAuth2ClientRepository(Map<String, ClientRegistration> clientRegistrations) {
        this.clientRegistrations = clientRegistrations;
    }

    public ClientRegistration findByRegistrationId(String registrationId) {
        return clientRegistrations.get(registrationId);
    }
}
