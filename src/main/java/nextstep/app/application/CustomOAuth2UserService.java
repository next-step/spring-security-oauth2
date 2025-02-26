package nextstep.app.application;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.oauth2.userinfo.DefaultOAuth2UserService;
import nextstep.oauth2.userinfo.OAuth2User;
import nextstep.oauth2.userinfo.OAuth2UserRequest;
import nextstep.oauth2.profile.OAuth2ProfileUser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);
        final Map<String, Object> attributes = oauth2User.attributes();

        final OAuth2ProfileUser profileUser = OAuth2ProfileUser.of(userRequest.registrationId(), attributes);
        String username = attributes.get(userRequest.userNameAttributeName()).toString();

        Member member = memberRepository.findByEmail(username)
                .orElseGet(() -> memberRepository.save(new Member(profileUser.getEmail(), "", profileUser.getName(), profileUser.getImageUrl(), Set.of("USER"))));

        return oauth2User;
    }
}
