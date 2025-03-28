package nextstep.security.oauth2.client.registration;

import java.util.Set;

public record ClientRegistration(
        String registrationId,
        String clientId,
        String clientSecret,
        String redirectUri,
        Set<String> scopes,
        ProviderDetails providerDetails
) {

    public record ProviderDetails(
            String authorizationUri,
            String tokenUri,
            String userInfoUri,
            String userNameAttribute
    ) {

    }
}
