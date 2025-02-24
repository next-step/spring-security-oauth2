package nextstep.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.Authentication;

public interface OAuth2AuthorizedClientRepository {
	<T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, Authentication principal,
			HttpServletRequest request);

	void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal,
			HttpServletRequest request, HttpServletResponse response);

	void removeAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request,
			HttpServletResponse response);

}
