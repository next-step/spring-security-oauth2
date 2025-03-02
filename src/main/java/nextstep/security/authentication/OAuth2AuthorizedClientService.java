package nextstep.security.authentication;

public interface OAuth2AuthorizedClientService {

    void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal);
}
