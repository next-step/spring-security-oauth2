package nextstep.oauth2.registration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public record OAuth2ClientProperties(
        Map<String, Provider> provider,
        Map<String, Registration> registration
) {
    public ClientRegistrationDao createClientRegistrationDao() {
        return new ClientRegistrationDao(getClientRegistration());
    }

    private Map<String, ClientRegistration> getClientRegistration() {
        return this.registration.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, this::getClientRegistration
        ));
    }

    private ClientRegistration getClientRegistration(
            Map.Entry<String, Registration> entry
    ) {
        final String registrationId = entry.getKey();
        final Registration registration = entry.getValue();
        final Provider provider = this.provider.get(registrationId);
        return new ClientRegistration(
                registrationId,
                registration.clientId(),
                registration.clientSecret(),
                registration.redirectUri(),
                registration.scope(),
                provider.getProviderDetails()
        );
    }

    public record Registration(
            String provider,
            String clientId,
            String clientSecret,
            String redirectUri,
            Set<String> scope
    ) {}

    public record Provider(
            String authorizationUri,
            String tokenUri,
            String userInfoUri,
            String userNameAttributeName
    ) {
        public ClientRegistration.ProviderDetails getProviderDetails() {
            return new ClientRegistration.ProviderDetails(
                    authorizationUri, tokenUri, getUserInfo()
            );
        }

        public ClientRegistration.UserInfoEndpoint getUserInfo() {
            return new ClientRegistration.UserInfoEndpoint(
                    userInfoUri, userNameAttributeName
            );
        }
    }
}
