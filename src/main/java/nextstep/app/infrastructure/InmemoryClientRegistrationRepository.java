package nextstep.app.infrastructure;

import nextstep.app.config.OAuth2Properties;
import nextstep.app.domain.ClientRegistration;
import nextstep.app.domain.ClientRegistrationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class InmemoryClientRegistrationRepository implements ClientRegistrationRepository {

    private final Map<String, ClientRegistration> clientRegistrations;

    public InmemoryClientRegistrationRepository(List<OAuth2Properties> properties) {
        this.clientRegistrations = properties.stream()
                .map(this::createClientRegistration)
                .collect(Collectors.toUnmodifiableMap(
                        ClientRegistration::registrationId,
                        Function.identity()
                ));
    }

    @Override
    public Optional<ClientRegistration> findByRegistrationId(String registrationId) {
        return Optional.ofNullable(clientRegistrations.get(registrationId));
    }


    private ClientRegistration createClientRegistration(OAuth2Properties properties) {
        return ClientRegistration.builder()
                .registrationId(properties.getRegistrationId())
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .redirectUri(properties.getAuthorization().getRedirectUri())
                .scope(properties.getScope())
                .build();
    }
}
