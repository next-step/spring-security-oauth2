package nextstep.oauth2.registration;

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
            UserInfoEndpoint userInfoEndpoint
    ) {}

    public record UserInfoEndpoint(
            String uri,
            String userNameAttributeName
    ) {}
}
