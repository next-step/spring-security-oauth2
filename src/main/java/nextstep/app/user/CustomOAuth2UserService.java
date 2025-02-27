package nextstep.app.user;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.oauth.OAuth2ProfileUser;
import nextstep.oauth.user.OAuth2User;
import nextstep.oauth.user.OAuth2UserRequest;
import nextstep.oauth.user.OAuth2UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

@Service
public class CustomOAuth2UserService implements OAuth2UserService {
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        String userNameAttributeName = userRequest.getClientRegistration().getUserNameAttributeName();
        Map<String, Object> attributes = requestUserInfo(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2ProfileUser profileUser = OAuth2ProfileUser.of(registrationId, attributes);

        String username = attributes.get(userNameAttributeName).toString();
        Member member = memberRepository.findByEmail(username)
                .orElseGet(() -> memberRepository.save(new Member(profileUser.getEmail(), "", profileUser.getName(), profileUser.getImageUrl(), Set.of("USER"))));

        return new OAuth2User() {
            @Override
            public Set<String> getAuthorities() {
                return member.getRoles();
            }

            @Override
            public Map<String, Object> getAttributes() {
                return attributes;
            }

            @Override
            public String getUserNameAttributeName() {
                return userNameAttributeName;
            }
        };
    }

    public Map<String, Object> requestUserInfo(OAuth2UserRequest userRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + userRequest.getAccessToken()); // Bearer 토큰을 사용한 인증

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userRequest.getClientRegistration().getUserInfoUri(),
                HttpMethod.GET,
                requestEntity,
                Map.class
        );

        return response.getBody();
    }
}
