package nextstep.app;

import nextstep.oauth2.authentication.provider.OAuth2LoginAuthenticationProvider;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.registration.OAuth2ClientProperties;
import nextstep.oauth2.userinfo.OAuth2UserService;
import nextstep.oauth2.web.authorizedclient.OAuth2AuthorizedClientRepository;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.DaoAuthenticationProvider;
import nextstep.security.authentication.ProviderManager;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@EnableConfigurationProperties(OAuth2ClientProperties.class)
@Configuration
public class OAuth2Config {
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
}
