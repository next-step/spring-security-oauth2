package nextstep.security.oauth2.client.registration;

public interface ClientRegistrationRepository {

    ClientRegistration findByRegistrationId(String registrationId);
}
