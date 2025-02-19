package nextstep.app.infrastructure;

import nextstep.app.domain.ClientRegistration;
import nextstep.app.domain.ClientRegistrationRepository;
import nextstep.security.authentication.oauth.OAuth2AuthenticationRequestStrategy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class InmemoryClientRegistrationRepository implements ClientRegistrationRepository {

    private final Map<String, ClientRegistration> clientRegistrations;

    public InmemoryClientRegistrationRepository(List<OAuth2AuthenticationRequestStrategy> strategies) {
        clientRegistrations = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        OAuth2AuthenticationRequestStrategy::getOAuth2Type,
                        getClientRegistrationFunction()
                ));
    }

    @Override
    public Optional<ClientRegistration> findByRegistrationId(String registrationId) {
        return Optional.ofNullable(clientRegistrations.get(registrationId));
    }

    private Function<OAuth2AuthenticationRequestStrategy, ClientRegistration> getClientRegistrationFunction() {
        return it ->
                ClientRegistration.builder()
                        .registrationId(it.getOAuth2Type())
                        .clientId(it.getClientId())
                        .clientSecret(it.getClientSecret())
                        .redirectUri(it.getRedirectUri())
                        .scope(it.getScope())
                        .build();
    }
}
