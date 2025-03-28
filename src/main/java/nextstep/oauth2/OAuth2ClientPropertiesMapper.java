package nextstep.oauth2;

import nextstep.oauth2.OAuth2ClientProperties.Provider;
import nextstep.oauth2.OAuth2ClientProperties.Registration;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

public class OAuth2ClientPropertiesMapper {

    private final OAuth2ClientProperties properties;

    public OAuth2ClientPropertiesMapper(OAuth2ClientProperties properties) {
        Assert.notNull(properties, "properties cannot be null");
        this.properties = properties;
    }

    public List<ClientRegistration> asClientRegistrations() {
        Map<String, Registration> registrations = properties.getRegistration();
        Map<String, Provider> providers = properties.getProvider();

        return registrations.keySet().stream()
                .map(registrationId -> {
                    Registration registration = registrations.get(registrationId);
                    Provider provider = providers.get(registration.provider());

                    return new ClientRegistration(
                            registrationId,
                            registration.clientId(),
                            registration.clientSecret(),
                            registration.redirectUri(),
                            registration.scope(),
                            new ClientRegistration.ProviderDetails(
                                    provider.authorizationUri(),
                                    provider.tokenUri(),
                                    provider.userInfoUri(),
                                    provider.userNameAttribute()
                            )
                    );
                })
                .toList();
    }
}
