package nextstep.app;

import nextstep.security.access.AnyRequestMatcher;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcherEntry;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.access.hierarchicalroles.RoleHierarchyImpl;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.security.authorization.AuthorityAuthorizationManager;
import nextstep.security.authorization.AuthorizationFilter;
import nextstep.security.authorization.AuthorizationManager;
import nextstep.security.authorization.PermitAllAuthorizationManager;
import nextstep.security.authorization.RequestAuthorizationManager;
import nextstep.security.authorization.SecuredMethodInterceptor;
import nextstep.security.config.DefaultSecurityFilterChain;
import nextstep.security.config.DelegatingFilterProxy;
import nextstep.security.config.FilterChainProxy;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.context.SecurityContextHolderFilter;
import nextstep.security.oauth2.AuthorizationRequestRepository;
import nextstep.security.oauth2.HttpSessionOAuth2AuthorizedClientRepository;
import nextstep.security.oauth2.OAuth2AuthorizedClientRepository;
import nextstep.security.oauth2.OAuth2LoginAuthenticationFilter;
import nextstep.security.oauth2.OAuth2AuthorizationRequestRedirectFilter;
import nextstep.security.oauth2.OAuth2AuthorizationRequestResolver;
import nextstep.security.oauth2.registration.OAuth2ClientProviderProperties;
import nextstep.security.oauth2.registration.OAuth2ClientRegistrationProperties;
import nextstep.security.oauth2.registration.ClientRegistration;
import nextstep.security.oauth2.registration.ClientRegistrationRepository;
import nextstep.security.oauth2.registration.InMemoryClientRegistrationRepository;
import nextstep.security.oauth2.user.OAuth2User;
import nextstep.security.oauth2.user.OAuth2UserRequest;
import nextstep.security.oauth2.user.OAuth2UserService;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@EnableAspectJAutoProxy
@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    public SecurityConfig(UserDetailsService userDetailsService, OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) {
        this.userDetailsService = userDetailsService;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy(ClientRegistrationRepository clientRegistrationRepository,
                                                       OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
                                                       AuthorizationRequestRepository authorizationRequestRepository) {
        return new DelegatingFilterProxy(filterChainProxy(List.of(securityFilterChain(clientRegistrationRepository, oAuth2AuthorizedClientRepository, authorizationRequestRepository))));
    }


    public FilterChainProxy filterChainProxy(List<SecurityFilterChain> securityFilterChains) {
        return new FilterChainProxy(securityFilterChains);
    }

    @Bean
    public SecuredMethodInterceptor securedMethodInterceptor() {
        return new SecuredMethodInterceptor();
    }


    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }

    @Bean
    public AuthorizationRequestRepository authorizationRequestRepository() {
        return new AuthorizationRequestRepository();
    }

    private SecurityFilterChain securityFilterChain(ClientRegistrationRepository clientRegistrationRepository,
                                                   OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
                                                   AuthorizationRequestRepository authorizationRequestRepository) {

        return new DefaultSecurityFilterChain(
                List.of(
                        new SecurityContextHolderFilter(),
                        new UsernamePasswordAuthenticationFilter(userDetailsService),
                        new BasicAuthenticationFilter(userDetailsService),
                        new OAuth2AuthorizationRequestRedirectFilter(new OAuth2AuthorizationRequestResolver(clientRegistrationRepository), authorizationRequestRepository),
                        new OAuth2LoginAuthenticationFilter(oAuth2UserService, clientRegistrationRepository, oAuth2AuthorizedClientRepository),
                        new AuthorizationFilter(requestAuthorizationManager())
                )
        );
    }


    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.with()
                .role("ADMIN").implies("USER")
                .build();
    }

    @Bean
    public RequestAuthorizationManager requestAuthorizationManager() {
        List<RequestMatcherEntry<AuthorizationManager>> mappings = new ArrayList<>();
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/members"), new AuthorityAuthorizationManager(roleHierarchy(), "ADMIN")));
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/members/me"), new AuthorityAuthorizationManager(roleHierarchy(), "USER")));
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/search"), new PermitAllAuthorizationManager()));
        mappings.add(new RequestMatcherEntry<>(AnyRequestMatcher.INSTANCE, new PermitAllAuthorizationManager()));
        return new RequestAuthorizationManager(mappings);
    }


    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties) {
        return new InMemoryClientRegistrationRepository(oAuth2ClientProperties.getRegistrations()
                .stream()
                .map((it) -> clientRegistration(it, oAuth2ClientProperties.getOauth2Registration(it), oAuth2ClientProperties.getOauth2Provider(it)))
                .toList()
        );
    }

    private ClientRegistration clientRegistration(String registrationId, OAuth2ClientRegistrationProperties oauth2Registration, OAuth2ClientProviderProperties oauth2Provider) {
        return ClientRegistration.builder(registrationId)
                .clientId(oauth2Registration.getClientId())
                .clientSecret(oauth2Registration.getClientSecret())
                .scope(oauth2Registration.getScope())
                .responseType(oauth2Registration.getResponseType())
                .redirectUri(oauth2Registration.getRedirectUri())
                .tokenUri(oauth2Provider.getTokenUri())
                .userInfoUri(oauth2Provider.getUserInfoUri())
                .authorizationUri(oauth2Provider.getAuthorizationUri())
                .build();
    }
}
