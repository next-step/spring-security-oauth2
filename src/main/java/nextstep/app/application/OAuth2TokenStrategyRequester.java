package nextstep.app.application;

import nextstep.app.domain.ClientRegistrationRepository;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.TokenResponse;
import nextstep.security.authentication.oauth.OAuth2TokenRequestStrategy;
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
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2TokenStrategyRequester(RestTemplate restTemplate, List<OAuth2TokenRequestStrategy> strategies, ClientRegistrationRepository clientRegistrationRepository) {
        this.restTemplate = restTemplate;
        this.strategies = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        OAuth2TokenRequestStrategy::getOAuth2Type,
                        strategy -> strategy
                ));
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public TokenResponse request(String registrationId, String code) {
        final var strategy = strategies.get(registrationId);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        }

        final var clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported registrationId: " + registrationId));

        try {
            final var request = strategy.requestToken(clientRegistration, code);
            final var requestUri = strategy.getRequestUri();
            final var response = restTemplate.postForEntity(
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
