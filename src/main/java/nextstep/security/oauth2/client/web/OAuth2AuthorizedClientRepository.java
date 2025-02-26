package nextstep.security.oauth2.client.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.client.OAuth2AuthorizedClient;

public interface OAuth2AuthorizedClientRepository {
    OAuth2AuthorizedClient loadAuthorizedClient(String clientRegistrationId, Authentication principal,
                                                HttpServletRequest request);

    void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal,
                              HttpServletRequest request, HttpServletResponse response);

    void removeAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request,
                                HttpServletResponse response);
}
