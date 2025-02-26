package nextstep.security.oauth2.client.userinfo;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.oauth2.client.authentication.OAuth2ProfileUser;
import nextstep.security.oauth2.client.authentication.OAuth2ProfileUserFactory;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.core.OAuth2AuthenticationException;
import nextstep.security.oauth2.core.user.DefaultOAuth2User;
import nextstep.security.oauth2.core.user.OAuth2User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class DefaultOAuth2UserService implements OAuth2UserService {
    private final MemberRepository memberRepository;

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final RestOperations restOperations;

    public DefaultOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.restOperations = new RestTemplate();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "userRequest cannot be null");
        String userNameAttributeName = getUserNameAttributeName(userRequest);
        RequestEntity<?> request = convertRequestEntity(userRequest);
        ResponseEntity<Map<String, Object>> response = getResponse(request);
        Map<String, Object> attributes = convertAttributes(userRequest, response.getBody());
        String email = attributes.get("email").toString();
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(
                        new Member(
                                email,
                                "",
                                attributes.get("name").toString(),
                                attributes.get("imageUrl").toString(),
                                Set.of("USER")
                        )
                ));
        return new DefaultOAuth2User(member, attributes, userNameAttributeName);
    }

    private String getUserNameAttributeName(OAuth2UserRequest userRequest) {
        if (!StringUtils
                .hasText(userRequest.clientRegistration().providerDetails().userInfoEndpoint().uri())) {
            throw new OAuth2AuthenticationException();
        }
        String userNameAttributeName = userRequest.clientRegistration()
                .providerDetails()
                .userInfoEndpoint()
                .userNameAttributeName();

        if (!StringUtils.hasText(userNameAttributeName)) {
            throw new OAuth2AuthenticationException();
        }

        return userNameAttributeName;
    }

    private RequestEntity<?> convertRequestEntity(OAuth2UserRequest userRequest) {
        ClientRegistration clientRegistration = userRequest.clientRegistration();

        HttpHeaders headers = new HttpHeaders();

        URI uri = UriComponentsBuilder
                .fromUriString(clientRegistration.providerDetails().userInfoEndpoint().uri())
                .build()
                .toUri();

        headers.setBearerAuth(userRequest.accessToken().tokenValue());

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new RequestEntity<>(headers, HttpMethod.GET, uri);
    }

    private Map<String, Object> convertAttributes(OAuth2UserRequest userRequest, Map<String, Object> body) {
        String clientRegistrationId = userRequest.clientRegistration().registrationId();

        OAuth2ProfileUser profileUser = OAuth2ProfileUserFactory.create(clientRegistrationId, body);

        Map<String, Object> convertedAttributes = new HashMap<>();
        convertedAttributes.put("name", profileUser.getName());
        convertedAttributes.put("imageUrl", profileUser.getImageUrl());
        convertedAttributes.put("email", profileUser.getEmail());

        return convertedAttributes;
    }

    private ResponseEntity<Map<String, Object>> getResponse(RequestEntity<?> request) {
        try {
            return this.restOperations.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
        } catch (Exception e) {
            throw new OAuth2AuthenticationException();
        }
    }
}

