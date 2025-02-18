package nextstep.app.application;

import nextstep.security.authentication.OAuth2AuthenticationRequestResolver;
import nextstep.security.authentication.OAuth2AuthenticationRequestStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OAuth2AuthenticationStrategyResolver implements OAuth2AuthenticationRequestResolver {
    private final Map<String, OAuth2AuthenticationRequestStrategy> strategies;

    public OAuth2AuthenticationStrategyResolver(List<OAuth2AuthenticationRequestStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
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

        return strategy.getBaseRequestUri()
                + "?response_type=" + OAuth2AuthenticationRequestStrategy.RESPONSE_TYPE
                + "&client_id=" + strategy.getClientId()
                + "&scope=" + strategy.getScope()
                + "&redirect_uri=" + strategy.getRedirectUri();
    }
}
