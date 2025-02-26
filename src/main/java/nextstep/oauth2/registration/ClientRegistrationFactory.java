package nextstep.oauth2.registration;

import nextstep.oauth2.OAuth2ClientProperties;

import java.util.HashMap;
import java.util.Map;

import static nextstep.oauth2.OAuth2ClientProperties.Provider;
import static nextstep.oauth2.OAuth2ClientProperties.Registration;

public class ClientRegistrationFactory {

    public static Map<String, ClientRegistration> createRegistrations(OAuth2ClientProperties properties) {
        Map<String, ClientRegistration> clientRegistrations = new HashMap<>();

        properties.getRegistration().forEach((registrationId, registration) -> {
            Provider provider = properties.getProvider().get(registrationId);

            if (provider == null) {
                throw new IllegalArgumentException("Provider 정보가 없습니다: " + registrationId);
            }

            clientRegistrations.put(registrationId, createClientRegistration(registrationId, registration, provider));
        });

        return clientRegistrations;
    }

    private static ClientRegistration createClientRegistration(String registrationId,
                                                               Registration registration,
                                                               Provider provider) {
        return ClientRegistration.builder()
                .registrationId(registrationId)
                .clientId(registration.getClientId())
                .clientSecret(registration.getClientSecret())
                .redirectUri(registration.getRedirectUri())
                .scopes(registration.getScope())
                .providerDetails(new ClientRegistration.ProviderDetails(
                        provider.getAuthorizationUri(),
                        provider.getTokenUri(),
                        provider.getUserInfoUri(),
                        provider.getUserNameAttributeName()
                ))
                .build();
    }
}
