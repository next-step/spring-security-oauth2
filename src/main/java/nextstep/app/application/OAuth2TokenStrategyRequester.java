package nextstep.app.application;

import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;
import nextstep.security.authentication.oauth.OAuth2TokenRequestStrategy;
import nextstep.security.authentication.TokenRequest;
import nextstep.security.authentication.TokenResponse;
import nextstep.security.authentication.oauth.OAuth2TokenRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OAuth2TokenStrategyRequester implements OAuth2TokenRequester {

    private static final Logger log = LoggerFactory.getLogger(OAuth2TokenStrategyRequester.class);

    private final RestTemplate restTemplate;
    private final Map<String, OAuth2TokenRequestStrategy> strategies;

    public OAuth2TokenStrategyRequester(RestTemplate restTemplate, List<OAuth2TokenRequestStrategy> strategies) {
        this.restTemplate = restTemplate;
        this.strategies = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        OAuth2TokenRequestStrategy::getOAuth2Type,
                        strategy -> strategy
                ));
    }

    @Override
    public TokenResponse request(OAuth2AuthorizationRequest authorizationRequest, String code) {
        OAuth2TokenRequestStrategy strategy = strategies.get(authorizationRequest.registrationId());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported OAuth2 auth2Type: " + authorizationRequest.registrationId());
        }

        try {
            TokenRequest request = strategy.requestToken(authorizationRequest, code);
            String requestUri = strategy.getRequestUri();
            var response = restTemplate.postForEntity(
                    requestUri,
                    request,
                    strategy.getResponseClass()
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new AuthenticationException("OAuth failed with " + response.getStatusCode());
            }

            return response.getBody();
        } catch (RestClientException ex) {
            log.error(ex.getMessage());
            throw new AuthenticationException();
        }
    }
}
