package nextstep.app;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.access.AnyRequestMatcher;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcherEntry;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.access.hierarchicalroles.RoleHierarchyImpl;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.BasicAuthenticationFilter;
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
import nextstep.security.oauth2.authorizedclient.InMemoryOAuth2AuthorizedClientService;
import nextstep.security.oauth2.authentication.OAuth2AuthorizationRequestRedirectFilter;
import nextstep.security.oauth2.authorizedclient.OAuth2AuthorizedClientService;
import nextstep.security.oauth2.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import nextstep.security.oauth2.authentication.OAuth2LoginAuthenticationFilter;
import nextstep.security.oauth2.authentication.OAuth2LoginAuthenticationProvider;
import nextstep.security.oauth2.client.ClientRegistration;
import nextstep.security.oauth2.client.ClientRegistrationRepository;
import nextstep.security.oauth2.client.ClientRegistrations;
import nextstep.security.oauth2.client.InMemoryClientRegistrationRepository;
import nextstep.security.oauth2.endpoint.OAuth2RemoteClientAdapter;
import nextstep.security.oauth2.exchange.AuthorizationRequestRepository;
import nextstep.security.oauth2.exchange.DefaultOAuth2AuthorizationRequestResolver;
import nextstep.security.oauth2.exchange.HttpSessionOAuth2AuthorizationRequestRepository;
import nextstep.security.oauth2.exchange.OAuth2AuthorizationRequestResolver;
import nextstep.security.oauth2.user.DefaultOAuth2UserService;
import nextstep.security.oauth2.user.OAuth2UserService;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EnableAspectJAutoProxy
@Configuration
@EnableConfigurationProperties({ClientRegistrations.class, ClientRegistration.class})
public class SecurityConfig {

    private final MemberRepository memberRepository;

    public SecurityConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy(OAuth2RemoteClientAdapter oAuth2RemoteClientAdapter,
                                                       ClientRegistrationRepository clientRegistrationRepository) {
        return new DelegatingFilterProxy(filterChainProxy(List.of(securityFilterChain(oAuth2RemoteClientAdapter,
                                                                                      clientRegistrationRepository))));
    }

    public FilterChainProxy filterChainProxy(List<SecurityFilterChain> securityFilterChains) {
        return new FilterChainProxy(securityFilterChains);
    }

    @Bean
    public SecuredMethodInterceptor securedMethodInterceptor() {
        return new SecuredMethodInterceptor();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistrations clientRegistrations) {
        return new InMemoryClientRegistrationRepository(clientRegistrations.getRegistration());
    }

    @Bean
    public OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        return new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
    }

    @Bean
    public AuthorizationRequestRepository authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2UserService oAuth2UserService(OAuth2RemoteClientAdapter oAuth2RemoteClientAdapter) {
        return new DefaultOAuth2UserService(oAuth2RemoteClientAdapter);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientRepository(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(OAuth2RemoteClientAdapter oAuth2RemoteClientAdapter,
                                                   ClientRegistrationRepository clientRegistrationRepository) {
        return new DefaultSecurityFilterChain(
                List.of(
                        new SecurityContextHolderFilter(),
                        new UsernamePasswordAuthenticationFilter(userDetailsService()),
                        new BasicAuthenticationFilter(userDetailsService()),
                        new OAuth2AuthorizationRequestRedirectFilter(
                                oAuth2AuthorizationRequestResolver(clientRegistrationRepository),
                                authorizationRequestRepository()
                        ),
                        new OAuth2LoginAuthenticationFilter(clientRegistrationRepository,
                                                            new ProviderManager(
                                                                    List.of(
                                                                            new OAuth2LoginAuthenticationProvider(
                                                                                    new OAuth2AuthorizationCodeAuthenticationProvider(
                                                                                            oAuth2RemoteClientAdapter),
                                                                                    oAuth2UserService(oAuth2RemoteClientAdapter)
                                                                            ))
                                                            ),
                                                            authorizedClientRepository(clientRegistrationRepository)),
                        new AuthorizationFilter(requestAuthorizationManager()))
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
}
