package nextstep.security.oauth2.registration;

public interface ClientRegistrationRepository {
	ClientRegistration findByRegistrationId(String registrationId);
}
