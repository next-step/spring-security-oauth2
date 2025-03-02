package nextstep.security.authentication;

import java.util.HashMap;
import java.util.Map;

public class InMemoryOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    private static final Map<OAuth2AuthorizedClientId, OAuth2AuthorizedClient> authorizedClients = new HashMap<>();

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        authorizedClients.put(
                new OAuth2AuthorizedClientId(authorizedClient.getClientRegistration().getClientId(), principal.toString()), authorizedClient);
    }
}
