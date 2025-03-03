package nextstep.security.authentication;

public interface OAuth2AuthorizedClientRepository {

    void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal);
}
