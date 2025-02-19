package nextstep.app.application;

import nextstep.security.authentication.OAuth2AuthenticationRequestResolver;
import nextstep.security.authentication.OAuth2AuthenticationRequestStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
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
    public String resolve(String oAuth2Type) {
        OAuth2AuthenticationRequestStrategy strategy = strategies.get(oAuth2Type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported OAuth2 type: " + oAuth2Type);
        }

        return UriComponentsBuilder.fromHttpUrl(strategy.getBaseRequestUri())
                .queryParam("response_type", OAuth2AuthenticationRequestStrategy.RESPONSE_TYPE)
                .queryParam("client_id", strategy.getClientId())
                .queryParam("scope", strategy.getScope())
                .queryParam("redirect_uri", strategy.getRedirectUri())
                .toUriString();
    }
}
