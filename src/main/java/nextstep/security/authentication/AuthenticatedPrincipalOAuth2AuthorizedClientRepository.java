package nextstep.security.authentication;

public class AuthenticatedPrincipalOAuth2AuthorizedClientRepository implements OAuth2AuthorizedClientRepository {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public AuthenticatedPrincipalOAuth2AuthorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        authorizedClientService.saveAuthorizedClient(authorizedClient, principal);
    }
}
