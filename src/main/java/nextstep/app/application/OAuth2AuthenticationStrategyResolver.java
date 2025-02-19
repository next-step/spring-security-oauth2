package nextstep.app.application;

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
                        OAuth2AuthenticationRequestStrategy::getOAuth2Type,
                        strategy -> strategy
                ));
    }

    @Override
    public OAuth2AuthorizationRequest resolve(OAuth2ClientRegistration clientRegistration) {
        OAuth2AuthenticationRequestStrategy strategy = findStrategyByType(clientRegistration.getRegistrationId());

        String randomState = UUID.randomUUID().toString();
        String authorizationUri = UriComponentsBuilder.fromHttpUrl(strategy.getBaseRequestUri())
                .queryParam("response_type", OAuth2AuthenticationRequestStrategy.RESPONSE_TYPE)
                .queryParam("client_id", strategy.getClientId())
                .queryParam("scope", strategy.getScope())
                .queryParam("state", randomState)
                .queryParam("redirect_uri", strategy.getRedirectUri())
                .toUriString();

        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.from(clientRegistration);


        return builder.authorizationUri(authorizationUri)
                .responseType(OAuth2AuthenticationRequestStrategy.RESPONSE_TYPE)
                .state(randomState)
                .build();
    }

    private OAuth2AuthenticationRequestStrategy findStrategyByType(String oAuth2Type) {
        OAuth2AuthenticationRequestStrategy strategy = strategies.get(oAuth2Type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported OAuth2 type: " + oAuth2Type);
        }
        return strategy;
    }
}
