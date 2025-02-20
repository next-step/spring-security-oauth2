package nextstep.security.oauth2.authorizedclient;

import nextstep.security.authentication.Authentication;

public interface OAuth2AuthorizedClientService {

    OAuth2AuthorizedClient loadAuthorizedClient(String clientRegistrationId, String principalName);

    void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal);

    void removeAuthorizedClient(String clientRegistrationId, String principalName);
}
