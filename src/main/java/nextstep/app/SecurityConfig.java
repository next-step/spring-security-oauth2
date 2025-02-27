package nextstep.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.app.infrastructure.InmemoryMemberRepository;
import nextstep.security.access.AnyRequestMatcher;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcherEntry;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.access.hierarchicalroles.RoleHierarchyImpl;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.authentication.DaoAuthenticationProvider;
import nextstep.security.authentication.OAuth2ClientProperties;
import nextstep.security.authentication.OAuth2ClientProperties.Provider;
import nextstep.security.authentication.OAuth2ClientProperties.Registration;
import nextstep.security.authentication.ProviderManager;
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
import nextstep.security.oauth2.client.AuthorizationRequestRepository;
import nextstep.security.oauth2.client.HttpSessionOAuth2AuthorizationRequestRepository;
import nextstep.security.oauth2.client.OAuth2AuthorizationRequestRedirectFilter;
import nextstep.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.client.registration.ClientRegistrationRepository;
import nextstep.security.oauth2.client.registration.ClientRegistrationRepository.InMemoryClientRegistrationRepository;
import nextstep.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import nextstep.security.oauth2.client.userinfo.OAuth2UserService;
import nextstep.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import nextstep.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2ClientProperties oAuth2ClientProperties;

    public SecurityConfig(MemberRepository memberRepository,
                          OAuth2ClientProperties oAuth2ClientProperties) {
        this.memberRepository = memberRepository;
        this.oAuth2UserService = new DefaultOAuth2UserService(memberRepository);
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    @Bean
    public MemberRepository memberRepository() {
        return new InmemoryMemberRepository();
    }

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy() {
        return new DelegatingFilterProxy(filterChainProxy(List.of(securityFilterChain())));
    }

    @Bean
    public FilterChainProxy filterChainProxy(List<SecurityFilterChain> securityFilterChains) {
        return new FilterChainProxy(securityFilterChains);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        Map<String, ClientRegistration> registrations = getClientRegistrations(oAuth2ClientProperties);
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public SecuredMethodInterceptor securedMethodInterceptor() {
        return new SecuredMethodInterceptor();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(
                new DaoAuthenticationProvider(userDetailsService()),
                new OAuth2LoginAuthenticationProvider(oAuth2UserService)));
    }

    @Bean
    public AuthorizationRequestRepository authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain() {
        return new DefaultSecurityFilterChain(
                List.of(
                        new SecurityContextHolderFilter(),
                        new UsernamePasswordAuthenticationFilter(userDetailsService()),
                        new BasicAuthenticationFilter(userDetailsService()),
                        new OAuth2AuthorizationRequestRedirectFilter(clientRegistrationRepository(), authorizationRequestRepository()),
                        new OAuth2LoginAuthenticationFilter(clientRegistrationRepository(),
                                new HttpSessionOAuth2AuthorizedClientRepository(), authenticationManager()),
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
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/members"),
                new AuthorityAuthorizationManager(roleHierarchy(), "ADMIN")));
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/members/me"),
                new AuthorityAuthorizationManager(roleHierarchy(), "USER")));
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/search"),
                new PermitAllAuthorizationManager()));
        mappings.add(new RequestMatcherEntry<>(AnyRequestMatcher.INSTANCE, new PermitAllAuthorizationManager()));
        return new RequestAuthorizationManager(mappings);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
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
        };
    }

    private Map<String, ClientRegistration> getClientRegistrations(OAuth2ClientProperties oAuth2ClientProperties) {
        return oAuth2ClientProperties.getRegistrations().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> toClientRegistration(entry.getKey(), entry.getValue())));
    }

    private ClientRegistration toClientRegistration(String registrationId,
                                                    Registration registration) {
        Provider provider = oAuth2ClientProperties.getProviders().get(registration.provider());
        return new ClientRegistration(
                registrationId,
                registration.clientId(),
                registration.clientSecret(),
                registration.redirectUri(),
                Set.copyOf(registration.scope()),
                registration.authorizationGrantType(),
                provider.name(),
                new ClientRegistration.ProviderDetails(
                        provider.authorizationUri(),
                        provider.tokenUri(),
                        new ClientRegistration.UserInfoEndpoint(
                                provider.userInfoUri(),
                                provider.userNameAttributeName()
                        )
                )
        );
    }
}
