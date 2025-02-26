package nextstep.security.oauth2.client;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.client.registration.ClientRegistrationRepository;
import nextstep.security.oauth2.core.OAuth2AuthorizationRequest;
import org.springframework.web.util.UriComponentsBuilder;

public class DefaultOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final String authorizationRequestBaseUri;

    public DefaultOAuth2AuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository,
            String authorizationRequestBaseUri) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizationRequestBaseUri = authorizationRequestBaseUri;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        String registrationId = resolveRegistrationId(request);
        if (registrationId == null) {
            return null;
        }
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);

        if (clientRegistration == null) {
            throw new AuthenticationException("Invalid Client Registration with Id: " + registrationId);
        }

        String authorizationRequestUri = UriComponentsBuilder
                .fromHttpUrl(clientRegistration.providerDetails().authorizationUri())
                .queryParam("client_id", clientRegistration.clientId())
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", clientRegistration.scope()))
                .queryParam("redirect_uri", clientRegistration.redirectUri())
                .build()
                .toUriString();

        return new OAuth2AuthorizationRequest(
                clientRegistration.providerDetails().authorizationUri(),
                clientRegistration.clientId(),
                clientRegistration.redirectUri(),
                clientRegistration.scope(),
                authorizationRequestUri
        );
    }

    private String resolveRegistrationId(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith(authorizationRequestBaseUri)) {
            return uri.substring(authorizationRequestBaseUri.length());
        }

        return null;
    }
}
