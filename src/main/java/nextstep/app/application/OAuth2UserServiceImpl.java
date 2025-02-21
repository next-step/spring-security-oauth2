package nextstep.app.application;

import nextstep.app.application.dto.OAuth2UserImpl;
import nextstep.app.domain.*;
import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;
import nextstep.security.authentication.oauth.OAuth2User;
import nextstep.security.userservice.OAuth2ClientRegistration;
import nextstep.security.userservice.OAuth2UserService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class OAuth2UserServiceImpl implements OAuth2UserService {

    private static final String USERNAME_ATTRIBUTE = "username";

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizationRecordRepository authorizationRecordRepository;
    private final MemberRepository memberRepository;

    public OAuth2UserServiceImpl(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizationRecordRepository authorizationRecordRepository, MemberRepository memberRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizationRecordRepository = authorizationRecordRepository;
        this.memberRepository = memberRepository;
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
                request.state()
        );
        authorizationRecordRepository.save(oAuth2AuthorizationRecord);
    }

    @Override
    public OAuth2AuthorizationRequest consumeOAuth2AuthorizationRequest(String state) {
        OAuth2AuthorizationRecord oAuth2AuthorizationRecord = authorizationRecordRepository.findByState(state)
                .orElseThrow(NoSuchElementException::new);

        authorizationRecordRepository.deleteOne(oAuth2AuthorizationRecord);

        return oAuth2AuthorizationRecord;
    }

    @Override
    public OAuth2User loadUserBy(String username) {
        Member member = memberRepository.findByEmail(username)
                .orElseGet(() -> memberRepository.save(Member.oAuthMember(username)));

        Map<String, Object> attributes = Map.of(USERNAME_ATTRIBUTE, member.getEmail());

        return new OAuth2UserImpl(attributes, member.getRoles());
    }
}
