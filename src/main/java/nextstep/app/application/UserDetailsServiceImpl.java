package nextstep.app.application;

import nextstep.app.application.dto.UserDetailsImpl;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.userservice.UserDetails;
import nextstep.security.userservice.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    public UserDetailsServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new AuthenticationException("존재하지 않는 사용자입니다."));

        return new UserDetailsImpl(member.getEmail(), member.getPassword(), member.getRoles());
    }
}
