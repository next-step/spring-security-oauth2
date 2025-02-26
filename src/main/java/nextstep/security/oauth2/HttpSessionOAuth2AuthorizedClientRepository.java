package nextstep.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nextstep.security.authentication.Authentication;

import java.util.HashMap;
import java.util.Map;

public final class HttpSessionOAuth2AuthorizedClientRepository implements OAuth2AuthorizedClientRepository {

	private static final String DEFAULT_AUTHORIZED_CLIENTS_ATTR_NAME = "DEFAULT_AUTHORIZED_CLIENTS_ATTR_NAME";

	public OAuth2AuthorizedClient loadAuthorizedClient(String clientRegistrationId,
													   Authentication principal, HttpServletRequest request) {

		return this.getAuthorizedClients(request).get(clientRegistrationId);
	}

	@Override
	public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, OAuth2AuthorizedClient> authorizedClients = this.getAuthorizedClients(request);

		authorizedClients.put(authorizedClient.getClientRegistration().getRegistrationId(), authorizedClient);

		request.getSession().setAttribute(DEFAULT_AUTHORIZED_CLIENTS_ATTR_NAME, authorizedClients);
	}

	@Override
	public void removeAuthorizedClient(String clientRegistrationId, Authentication principal,
			HttpServletRequest request, HttpServletResponse response) {

		final Map<String, OAuth2AuthorizedClient> authorizedClients = this.getAuthorizedClients(request);

		if (authorizedClients.get(clientRegistrationId) != null) {
			authorizedClients.remove(clientRegistrationId);
		}
	}


	private Map<String, OAuth2AuthorizedClient> getAuthorizedClients(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		Map<String, OAuth2AuthorizedClient> authorizedClients = new HashMap<>();

		if (session != null) {
			authorizedClients = (Map<String, OAuth2AuthorizedClient>) session.getAttribute(DEFAULT_AUTHORIZED_CLIENTS_ATTR_NAME);
		}

		if (authorizedClients == null) {
			return new HashMap<>();
		}

		return authorizedClients;
	}

}
