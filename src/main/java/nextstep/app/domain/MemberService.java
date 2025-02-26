package nextstep.app.domain;

import nextstep.security.authentication.AuthenticationException;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;

import java.util.Set;

public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new AuthenticationException("존재하지 않는 사용자입니다."));

        return new UserDetails() {
            @Override
            public String getUsername() {
                return member.getEmail();
            }

            @Override
            public String getPassword() {
                return member.getPassword();
            }

            @Override
            public Set<String> getAuthorities() {
                return member.getRoles();
            }
        };
    }
}
