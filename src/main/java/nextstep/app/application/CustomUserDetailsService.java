package nextstep.app.application;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new AuthenticationException("존재하지 않는 사용자입니다."));

        return new CustomUserDetails(member.getEmail(), member.getPassword(), member.getRoles());
    }

    @Override
    public Optional<UserDetails> loadUser(String principal) {
        Optional<Member> optionalMember = memberRepository.findByEmail(principal);

        if (optionalMember.isEmpty()) {
            return Optional.empty();
        }

        Member member = optionalMember.get();

        return Optional.of(new CustomUserDetails(member.getEmail(), member.getPassword(), member.getRoles()));
    }


    @Override
    public void saveUser(String principal) {
        Member member = new Member(principal, null, null, null ,null);
        memberRepository.save(member);
    }
}
