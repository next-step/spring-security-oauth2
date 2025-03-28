package nextstep.app.security;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import nextstep.security.oauth2.client.userinfo.OAuth2UserRequest;
import nextstep.security.oauth2.core.user.OAuth2User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String username = generateUsername(userRequest, oAuth2User);

        return memberRepository.findByEmail(username)
                .map(member -> new CustomOAuth2User(member.getEmail()))
                .orElseGet(() -> registerNewMember(username));
    }

    private CustomOAuth2User registerNewMember(String username) {
        Member newMember = Member.byOAuth2Joining(username);
        memberRepository.save(newMember);
        logger.info("Welcome! New member joined!!!: username={}", newMember.getEmail());

        return new CustomOAuth2User(newMember.getEmail());
    }

    private String generateUsername(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        return userRequest.registration().registrationId() + "_" + oAuth2User.getName();
    }
}
