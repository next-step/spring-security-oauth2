package nextstep.security.oauth2.authorizedclient;

import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.client.ClientRegistration;
import nextstep.security.oauth2.client.ClientRegistrationRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {
    private final Map<OAuth2AuthorizedClientId, OAuth2AuthorizedClient> authorizedClients;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public InMemoryOAuth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        this.authorizedClients = new HashMap<>();
        this.clientRegistrationRepository = clientRegistrationRepository;
    }


    @Override
    public OAuth2AuthorizedClient loadAuthorizedClient(String clientRegistrationId, String principalName) {
        ClientRegistration registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (registration == null) {
            return null;
        }
        return this.authorizedClients.get(new OAuth2AuthorizedClientId(clientRegistrationId, principalName));
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        this.authorizedClients.put(new OAuth2AuthorizedClientId(
                authorizedClient.getClientRegistration().getRegistrationId(), principal.getName()), authorizedClient);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        ClientRegistration registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (registration != null) {
            this.authorizedClients.remove(new OAuth2AuthorizedClientId(clientRegistrationId, principalName));
        }
    }

    public static class OAuth2AuthorizedClientId {
        private final String clientRegistrationId;
        private final String principalName;

        public OAuth2AuthorizedClientId(String clientRegistrationId, String principalName) {
            this.clientRegistrationId = clientRegistrationId;
            this.principalName = principalName;
        }

        public String getClientRegistrationId() {
            return clientRegistrationId;
        }

        public String getPrincipalName() {
            return principalName;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            OAuth2AuthorizedClientId that = (OAuth2AuthorizedClientId) obj;
            return Objects.equals(this.clientRegistrationId, that.clientRegistrationId)
                    && Objects.equals(this.principalName, that.principalName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.clientRegistrationId, this.principalName);
        }
    }
}
