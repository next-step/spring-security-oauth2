package nextstep.oauth2.registration;

public interface ClientRegistrationRepository {
    ClientRegistration findByRegistrationId(String registrationId);
}
