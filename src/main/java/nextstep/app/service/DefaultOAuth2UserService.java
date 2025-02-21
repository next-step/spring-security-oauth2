package nextstep.app.service;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.oauth2.profile.OAuth2ProfileUser;
import nextstep.oauth2.userinfo.OAuth2User;
import nextstep.oauth2.userinfo.OAuth2UserRequest;
import nextstep.oauth2.userinfo.OAuth2UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

@Service
public class DefaultOAuth2UserService implements OAuth2UserService {
    private static final RestTemplate rest = new RestTemplate();

    private final MemberRepository memberRepository;

    public DefaultOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        final String userNameAttributeName = userRequest.clientRegistration()
                .providerDetails().userInfoEndpoint().userNameAttributeName();
        final Map<String, Object> attributes = exchangeAttributes(userRequest);
        final OAuth2ProfileUser profileUser = OAuth2ProfileUser.of(
                userRequest.clientRegistration().registrationId(),
                attributes
        );
        final Set<String> authorities = memberRepository.findByEmail(
                attributes.get(userNameAttributeName).toString()
        ).orElseGet(
                () -> memberRepository.save(new Member(
                        profileUser.email(), "", profileUser.name(),
                        profileUser.imageUrl(), Set.of("USER")
                ))
        ).getRoles();
        return OAuth2User.of(authorities, attributes, userNameAttributeName);
    }

    private Map exchangeAttributes(OAuth2UserRequest userRequest) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + userRequest.accessToken().token());
        return rest.exchange(
                userRequest.clientRegistration().providerDetails().userInfoEndpoint().uri(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        ).getBody();
    }
}
