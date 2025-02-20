package nextstep.oauth2.web.authorizedclient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nextstep.security.authentication.Authentication;

import java.util.HashMap;
import java.util.Map;

public class HttpSessionOAuth2AuthorizedClientRepository implements OAuth2AuthorizedClientRepository {
    private static final String ATTRIBUTE_NAME = "AUTHORIZED_CLIENTS";

    private HttpSessionOAuth2AuthorizedClientRepository() {}

    public static HttpSessionOAuth2AuthorizedClientRepository getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public OAuth2AuthorizedClient loadAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request) {
        return getAuthorizedClients(request).get(clientRegistrationId);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
        final Map<String, OAuth2AuthorizedClient> authorizedClients = getAuthorizedClients(request);
        authorizedClients.put(
                authorizedClient.clientRegistration().registrationId(),
                authorizedClient
        );
        request.getSession().setAttribute(ATTRIBUTE_NAME, authorizedClients);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
        getAuthorizedClients(request).remove(clientRegistrationId);
    }

    private Map<String, OAuth2AuthorizedClient> getAuthorizedClients(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        return session == null
                ? new HashMap<>()
                : getAuthorizedClients(session);
    }

    @SuppressWarnings("unchecked")
    private Map<String, OAuth2AuthorizedClient> getAuthorizedClients(HttpSession session) {
        var authorizedClients = session.getAttribute(ATTRIBUTE_NAME);
        return authorizedClients == null
                ? new HashMap<>()
                : (Map<String, OAuth2AuthorizedClient>) authorizedClients;
    }

    private static class SingletonHolder {
        private static final HttpSessionOAuth2AuthorizedClientRepository INSTANCE = new HttpSessionOAuth2AuthorizedClientRepository();
    }
}
