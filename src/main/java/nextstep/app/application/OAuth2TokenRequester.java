package nextstep.app.application;

import nextstep.app.application.dto.TokenRequest;
import nextstep.app.application.dto.TokenResponse;
import nextstep.security.authentication.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuth2TokenRequester {

    @Value("${oauth2.google.client-id}")
    private String clientId;

    @Value("${oauth2.google.client-secret}")
    private String clientSecret;

    @Value("${oauth2.google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final Logger log = LoggerFactory.getLogger(OAuth2TokenRequester.class);

    public OAuth2TokenRequester(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TokenResponse request(String code) {
        final var request = TokenRequest.of(code, clientId, clientSecret, redirectUri);
        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    TOKEN_URI,
                    request,
                    TokenResponse.class
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
