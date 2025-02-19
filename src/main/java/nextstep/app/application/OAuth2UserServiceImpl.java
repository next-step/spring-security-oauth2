package nextstep.app.application;

import nextstep.app.domain.ClientRegistrationRepository;
import nextstep.app.domain.OAuth2AuthorizationRecord;
import nextstep.app.domain.OAuth2AuthorizationRecordRepository;
import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;
import nextstep.security.userservice.OAuth2ClientRegistration;
import nextstep.security.userservice.OAuth2UserService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class OAuth2UserServiceImpl implements OAuth2UserService {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizationRecordRepository authorizationRecordRepository;

    public OAuth2UserServiceImpl(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizationRecordRepository authorizationRecordRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizationRecordRepository = authorizationRecordRepository;
    }


    @Override
    public OAuth2ClientRegistration loadClientRegistrationByRegistrationId(String registrationId) {
        return clientRegistrationRepository.findByRegistrationId(registrationId)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void saveOAuth2AuthorizationRequest(OAuth2AuthorizationRequest request) {
        var oAuth2AuthorizationRecord = OAuth2AuthorizationRecord.of(
                request.registrationId(),
                request.authorizationUri(),
                request.responseType(),
                request.clientId(),
                request.clientSecret(),
                request.redirectUri(),
                request.scope(),
                request.state()
        );
        authorizationRecordRepository.save(oAuth2AuthorizationRecord);
    }

    @Override
    public OAuth2AuthorizationRequest consumeOAuth2AuthorizationRequest(String state) {
        OAuth2AuthorizationRecord oAuth2AuthorizationRecord = authorizationRecordRepository.findByState(state)
                .orElseThrow(NoSuchElementException::new);

        authorizationRecordRepository.deleteOne(oAuth2AuthorizationRecord);

        return oAuth2AuthorizationRecord.toRequest();
    }
}
