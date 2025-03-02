package nextstep.oauth;

import java.util.Set;

public class ClientRegistrationRepository {
    private final OAuth2ClientProperties oAuth2ClientProperties;

    public ClientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    public ClientRegistration findByProviderKey(String providerKey) {
        OAuth2ClientProperties.Provider provider = oAuth2ClientProperties.getProvider().get(providerKey);
        OAuth2ClientProperties.Registration registration = oAuth2ClientProperties.getRegistration().get(providerKey);

        String registrationId = registration.getProvider();
        String clientId = registration.getClientId();
        String clientSecret = registration.getClientSecret();
        String redirectUri = registration.getRedirectUri();
        Set<String> scopes = registration.getScope();
        String authorizationUri = provider.getAuthorizationUri();
        String tokenUri = provider.getTokenUri();
        String userInfoUri = provider.getUserInfoUri();
        String userNameAttributeName = provider.getUserNameAttributeName();
        return new ClientRegistration(registrationId, clientId, clientSecret, redirectUri, scopes
                , authorizationUri, tokenUri, userInfoUri, userNameAttributeName);
    }
}
