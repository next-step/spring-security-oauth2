package nextstep.app;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.oauth2.authentication.provider.OAuth2LoginAuthenticationProvider;
import nextstep.oauth2.profile.OAuth2ProfileUser;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.registration.OAuth2ClientProperties;
import nextstep.oauth2.userinfo.OAuth2User;
import nextstep.oauth2.userinfo.OAuth2UserRequest;
import nextstep.oauth2.userinfo.OAuth2UserService;
import nextstep.oauth2.web.authorizedclient.OAuth2AuthorizedClientRepository;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.DaoAuthenticationProvider;
import nextstep.security.authentication.ProviderManager;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

@EnableConfigurationProperties(OAuth2ClientProperties.class)
@Configuration
public class OAuth2Config {
    private final RestTemplate rest = new RestTemplate();

    @Bean
    public ClientRegistrationRepository registrationRepository(
            OAuth2ClientProperties oauth2ClientProperties
    ) {
        return oauth2ClientProperties.createClientRegistrationDao();
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return OAuth2AuthorizedClientRepository.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            OAuth2UserService oAuth2UserService
    ) {
        return new ProviderManager(List.of(
                new DaoAuthenticationProvider(userDetailsService),
                new OAuth2LoginAuthenticationProvider(oAuth2UserService)
        ));
    }

    @Bean
    public OAuth2UserService oauth2UserService(
            MemberRepository memberRepository
    ) {
        return new OAuth2UserService() {
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
        };
    }
}
