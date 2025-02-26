package nextstep.security.oauth2.client;

public interface OAuth2AuthorizedClientService {

    OAuth2AuthorizedClient loadAuthorizedClient(String clientRegistrationId, String principalName);

    void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, String principalName);

    void removeAuthorizedClient(String clientRegistrationId, String principalName);
}
