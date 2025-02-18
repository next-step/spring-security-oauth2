package nextstep.app.ui;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.authentication.Authentication;
import nextstep.security.authorization.Secured;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.oauth2.user.OAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/members")
    public ResponseEntity<List<Member>> list() {
        List<Member> members = memberRepository.findAll();
        return ResponseEntity.ok(members);
    }

    @Secured("ADMIN")
    @GetMapping("/search")
    public ResponseEntity<List<Member>> search() {
        List<Member> members = memberRepository.findAll();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/members/me")
    public ResponseEntity<Member> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = extractEmail(authentication);
        Member member = memberRepository.findByEmail(email)
                                        .orElseThrow(RuntimeException::new);

        return ResponseEntity.ok(member);
    }

    private String extractEmail(Authentication authentication) {
        if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        if (authentication.getPrincipal() instanceof OAuth2User) {
            return ((OAuth2User) authentication.getPrincipal()).getEmail();
        }
        return "";
    }
}
