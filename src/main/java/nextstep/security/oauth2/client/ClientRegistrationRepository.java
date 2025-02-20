package nextstep.security.oauth2.client;

public interface ClientRegistrationRepository {

    ClientRegistration findByRegistrationId(String registrationId);
}
