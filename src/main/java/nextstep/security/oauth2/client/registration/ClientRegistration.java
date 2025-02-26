package nextstep.security.oauth2.client.registration;

import java.util.Set;

public record ClientRegistration(
        String registrationId,
        String clientId,
        String clientSecret,
        String redirectUri,
        Set<String> scope,
        String authorizationGrantType,
        String clientName,
        ProviderDetails providerDetails
) {
    public record ProviderDetails(
            String authorizationUri,
            String tokenUri,
            UserInfoEndpoint userInfoEndpoint
    ) {
    }

    public record UserInfoEndpoint(String uri,
                                   String userNameAttributeName
    ) {
    }
}
