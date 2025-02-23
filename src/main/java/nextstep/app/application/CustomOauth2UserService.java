package nextstep.app.application;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.oauth2.user.OAuth2UserService;
import nextstep.security.oauth2.user.Oauth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomOauth2UserService implements OAuth2UserService {
    private final MemberRepository memberRepository;

    public CustomOauth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Oauth2User loadUser(String principal) {
        final Member member = memberRepository.findByEmail(principal)
                .orElseGet(() -> memberRepository.save(new Member(principal, null, null, null, Set.of())));

        return new CustomOauth2User(member.getEmail(), member.getRoles());
    }
}
