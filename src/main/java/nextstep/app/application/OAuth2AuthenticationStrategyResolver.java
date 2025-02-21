package nextstep.app.application;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.app.domain.OAuth2AuthorizationRecord;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.oauth.OAuth2AuthenticationRequestResolver;
import nextstep.security.authentication.oauth.OAuth2AuthenticationRequestStrategy;
import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;
import nextstep.security.userservice.OAuth2ClientRegistration;
import nextstep.security.userservice.OAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OAuth2AuthenticationStrategyResolver implements OAuth2AuthenticationRequestResolver {

    private final Map<String, OAuth2AuthenticationRequestStrategy> strategies;
    private final OAuth2UserService auth2UserService;

    public OAuth2AuthenticationStrategyResolver(List<OAuth2AuthenticationRequestStrategy> strategies, OAuth2UserService auth2UserService) {
        this.strategies = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        OAuth2AuthenticationRequestStrategy::getRegistrationId,
                        strategy -> strategy
                ));
        this.auth2UserService = auth2UserService;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) throws AuthenticationException {
        String registrationId = getRegistrationId(request.getRequestURI());
        OAuth2ClientRegistration clientRegistration = auth2UserService.loadClientRegistrationByRegistrationId(registrationId);

        OAuth2AuthenticationRequestStrategy strategy = strategies.get(clientRegistration.getRegistrationId());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported registrationId: " + clientRegistration.getRegistrationId());
        }

        String randomState = UUID.randomUUID().toString();

        String scopeValue = String.join(" ", clientRegistration.getScope());
        String authorizationUri = UriComponentsBuilder.fromHttpUrl(strategy.getBaseRequestUri())
                .queryParam("response_type", OAuth2AuthenticationRequestStrategy.RESPONSE_TYPE)
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("scope", scopeValue)
                .queryParam("state", randomState)
                .queryParam("redirect_uri",clientRegistration.getRedirectUri())
                .toUriString();

        return new OAuth2AuthorizationRecord(clientRegistration.getRegistrationId(), authorizationUri, randomState);
    }

    private String getRegistrationId(String requestUri) {
        try {
            return requestUri.substring(requestUri.lastIndexOf("/") + 1);
        }  catch (NullPointerException e) {
            throw new AuthenticationException();
        }
    }
}
