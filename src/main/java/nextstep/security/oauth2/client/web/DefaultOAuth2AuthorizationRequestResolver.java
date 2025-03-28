package nextstep.security.oauth2.client.web;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.client.registration.ClientRegistrationRepository;
import nextstep.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.util.UriComponentsBuilder;

public class DefaultOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final String authorizationRequestBaseUri;
    private final ClientRegistrationRepository registrationRepository;

    public DefaultOAuth2AuthorizationRequestResolver(
            String authorizationRequestBaseUri,
            ClientRegistrationRepository registrationRepository
    ) {
        this.authorizationRequestBaseUri = authorizationRequestBaseUri;
        this.registrationRepository = registrationRepository;
    }

    @Override
    @Nullable
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        String registrationId = resolveRegistrationId(request);

        if (registrationId == null) {
            return null;
        }

        return resolve(registrationId);
    }

    @Nullable
    private String resolveRegistrationId(HttpServletRequest request) {
        String requestUri = request.getRequestURI();

        if (requestUri.startsWith(authorizationRequestBaseUri)) {
            return requestUri.substring(authorizationRequestBaseUri.length());
        }

        return null;
    }

    private OAuth2AuthorizationRequest resolve(String registrationId) {
        ClientRegistration registration = registrationRepository.findByRegistrationId(registrationId);
        if (registration == null) {
            throw new AuthenticationException("Cannot find ClientRegistration with registrationId: " + registrationId);
        }

        String redirectUri = UriComponentsBuilder.fromHttpUrl(registration.providerDetails().authorizationUri())
                .queryParam("client_id", registration.clientId())
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", registration.scopes()))
                .queryParam("redirect_uri", registration.redirectUri())
                .build()
                .toUriString();

        return new OAuth2AuthorizationRequest(registrationId, redirectUri);
    }
}
