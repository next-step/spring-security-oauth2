package nextstep.app.application;

import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.TokenResponse;
import nextstep.security.authentication.UserResponse;
import nextstep.security.authentication.oauth.OAuth2EmailResolveStrategy;
import nextstep.security.authentication.oauth.OAuth2EmailResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OAuth2EmailStrategyResolver implements OAuth2EmailResolver {
    private static final Logger log = LoggerFactory.getLogger(OAuth2EmailStrategyResolver.class);

    private final RestTemplate restTemplate;
    private final Map<String, OAuth2EmailResolveStrategy> strategies;

    public OAuth2EmailStrategyResolver(RestTemplate restTemplate, List<OAuth2EmailResolveStrategy> strategies) {
        this.restTemplate = restTemplate;
        this.strategies = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        OAuth2EmailResolveStrategy::getRegistrationId,
                        strategy -> strategy
                ));
    }

    @Override
    public String resolve(String registrationId, TokenResponse token) {
        OAuth2EmailResolveStrategy strategy = strategies.get(registrationId);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.getAccessToken());
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<? extends UserResponse> response = restTemplate.exchange(
                    strategy.getRequestUri(),
                    HttpMethod.GET,
                    entity,
                    strategy.getUserResponseClass()
            );

            UserResponse userResponse = response.getBody();
            if (userResponse == null || !StringUtils.hasText(userResponse.getEmail())) {
                throw new AuthenticationException(registrationId + " 에서 유저 email을 가져오는데 실패했습니다. userResponse: " + userResponse);
            }

            return userResponse.getEmail();

        } catch (RestClientException ex) {
            log.error(ex.getMessage());
            throw new AuthenticationException();
        }
    }
}
