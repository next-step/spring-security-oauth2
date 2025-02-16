package nextstep.oauth2.web.authorizationrequest;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationRequest;
import nextstep.oauth2.keygen.DefaultStateGenerator;
import nextstep.oauth2.keygen.StateGenerator;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import org.springframework.web.util.UriComponentsBuilder;

import static nextstep.oauth2.web.OAuth2ParameterNames.CLIENT_ID;
import static nextstep.oauth2.web.OAuth2ParameterNames.CODE;
import static nextstep.oauth2.web.OAuth2ParameterNames.REDIRECT_URI;
import static nextstep.oauth2.web.OAuth2ParameterNames.RESPONSE_TYPE;
import static nextstep.oauth2.web.OAuth2ParameterNames.SCOPE;
import static nextstep.oauth2.web.OAuth2ParameterNames.STATE;

public class OAuth2AuthorizationRequestResolver {
    private static final StateGenerator STATE_GENERATOR = DefaultStateGenerator.getInstance();

    private final String authorizationRequestBaseUri;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2AuthorizationRequestResolver(String authorizationRequestBaseUri, ClientRegistrationRepository clientRegistrationRepository) {
        this.authorizationRequestBaseUri = authorizationRequestBaseUri;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        final String registrationId = resolveRegistrationId(request);
        return registrationId == null ? null : resolve(registrationId);
    }

    private String resolveRegistrationId(HttpServletRequest request) {
        final String uri = request.getRequestURI();
        return uri.startsWith(authorizationRequestBaseUri)
                ? uri.substring(authorizationRequestBaseUri.length())
                : null;
    }

    private OAuth2AuthorizationRequest resolve(String registrationId) {
        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        final String state = STATE_GENERATOR.generateKey();
        return new OAuth2AuthorizationRequest(
                clientRegistration.providerDetails().authorizationUri(),
                clientRegistration.clientId(),
                clientRegistration.redirectUri(),
                clientRegistration.scopes(),
                state,
                authorizationRequestUri(clientRegistration, state)
        );
    }

    private String authorizationRequestUri(
            ClientRegistration clientRegistration,
            String state
    ) {
        return clientRegistration.providerDetails().authorizationUri()
                + "?"
                + UriComponentsBuilder.newInstance()
                .queryParam(CLIENT_ID, clientRegistration.clientId())
                .queryParam(RESPONSE_TYPE, CODE)
                .queryParam(SCOPE, clientRegistration.scopes())
                .queryParam(REDIRECT_URI, clientRegistration.redirectUri())
                .queryParam(STATE, state)
                .build()
                .toUri()
                .getQuery();
    }
}
