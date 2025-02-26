package nextstep.app.application;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.oauth2.OAuth2AuthenticationException;
import nextstep.security.oauth2.OAuth2ProviderClient;
import nextstep.security.oauth2.OAuth2UserInfo;
import nextstep.security.oauth2.user.OAuth2UserRequest;
import nextstep.security.oauth2.user.OAuth2UserService;
import nextstep.security.oauth2.user.Oauth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, Oauth2User> {
    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Oauth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2ProviderClient oAuth2ProviderClient = new OAuth2ProviderClient(userRequest.getClientRegistration());

        OAuth2UserInfo userInfo = oAuth2ProviderClient.getUserInfo(userRequest.getAccessToken());

        final Member member = memberRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> memberRepository.save(new Member(userInfo.getEmail(), null, null, null, Set.of())));


        return new CustomOauth2User(member.getEmail(), member.getRoles());
    }
}
