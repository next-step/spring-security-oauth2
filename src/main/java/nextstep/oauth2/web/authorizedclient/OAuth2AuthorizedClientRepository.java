package nextstep.oauth2.web.authorizedclient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.Authentication;

public interface OAuth2AuthorizedClientRepository {
    static OAuth2AuthorizedClientRepository getInstance() {
        return HttpSessionOAuth2AuthorizedClientRepository.getInstance();
    }

    OAuth2AuthorizedClient loadAuthorizedClient(
            String clientRegistrationId,
            Authentication principal,
            HttpServletRequest request
    );

    void saveAuthorizedClient(
            OAuth2AuthorizedClient authorizedClient,
            Authentication principal,
            HttpServletRequest request,
            HttpServletResponse response
    );

    void removeAuthorizedClient(
            String clientRegistrationId,
            Authentication principal,
            HttpServletRequest request,
            HttpServletResponse response
    );
}
