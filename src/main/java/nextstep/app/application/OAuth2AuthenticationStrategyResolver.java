package nextstep.app.application;

import nextstep.app.domain.OAuth2AuthorizationRecord;
import nextstep.security.authentication.oauth.OAuth2AuthenticationRequestResolver;
import nextstep.security.authentication.oauth.OAuth2AuthenticationRequestStrategy;
import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;
import nextstep.security.userservice.OAuth2ClientRegistration;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OAuth2AuthenticationStrategyResolver implements OAuth2AuthenticationRequestResolver {
    private final Map<String, OAuth2AuthenticationRequestStrategy> strategies;

    public OAuth2AuthenticationStrategyResolver(List<OAuth2AuthenticationRequestStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        OAuth2AuthenticationRequestStrategy::getRegistrationId,
                        strategy -> strategy
                ));
    }

    @Override
    public OAuth2AuthorizationRequest resolve(OAuth2ClientRegistration clientRegistration) {
        OAuth2AuthenticationRequestStrategy strategy = strategies.get(clientRegistration.getRegistrationId());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported registrationId: " + clientRegistration.getRegistrationId());
        }

        String randomState = UUID.randomUUID().toString();

        String authorizationUri = UriComponentsBuilder.fromHttpUrl(strategy.getBaseRequestUri())
                .queryParam("response_type", OAuth2AuthenticationRequestStrategy.RESPONSE_TYPE)
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("scope", clientRegistration.getScope())
                .queryParam("state", randomState)
                .queryParam("redirect_uri",clientRegistration.getRedirectUri())
                .toUriString();

        return new OAuth2AuthorizationRecord(clientRegistration.getRegistrationId(), authorizationUri, randomState);
    }

}
