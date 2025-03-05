package nextstep.app;

import nextstep.app.application.MemberService;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.app.oauth2.OAuth2AuthenticationSuccessHandler;
import nextstep.security.access.AntRequestMatcher;
import nextstep.security.access.AnyRequestMatcher;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcherEntry;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.access.hierarchicalroles.RoleHierarchyImpl;
import nextstep.security.authentication.*;
import nextstep.security.authorization.*;
import nextstep.security.config.DefaultSecurityFilterChain;
import nextstep.security.config.DelegatingFilterProxy;
import nextstep.security.config.FilterChainProxy;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContextHolderFilter;
import nextstep.security.oauth2.*;
import nextstep.security.oauth2.login.OAuth2LoginAuthenticationProvider;
import nextstep.security.oauth2.userdetails.OAuth2UserDetailsService;
import nextstep.security.properties.ClientRegistrationRepository;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EnableAspectJAutoProxy
@Configuration
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final List<OAuth2UserDetailsService> oAuth2UserDetailsServices;

    private final MemberService memberService;

    public SecurityConfig(final MemberRepository memberRepository,
                          final InMemoryClientRegistrationRepository clientRegistrationRepository,
                          final List<OAuth2UserDetailsService> oAuth2UserDetailsServices,
                          final MemberService memberService) {
        this.memberRepository = memberRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.oAuth2UserDetailsServices = oAuth2UserDetailsServices;
        this.memberService = memberService;
    }

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy() {
        return new DelegatingFilterProxy(filterChainProxy(List.of(securityFilterChain())));
    }

    public FilterChainProxy filterChainProxy(List<SecurityFilterChain> securityFilterChains) {
        return new FilterChainProxy(securityFilterChains);
    }

    @Bean
    public SecuredMethodInterceptor securedMethodInterceptor() {
        return new SecuredMethodInterceptor();
    }

    @Bean
    public SecurityFilterChain securityFilterChain() {
        return new DefaultSecurityFilterChain(List.of(new SecurityContextHolderFilter(httpSessionSecurityContextRepository()),
                new UsernamePasswordAuthenticationFilter(userDetailsService(), new UsernamePasswordAuthenticationSuccessHandler()),
                new BasicAuthenticationFilter(userDetailsService()),
                new OAuth2AuthorizationRequestRedirectFilter(
                        new AntRequestMatcher(HttpMethod.GET, "/oauth2/authorization/**"),
                        oAuth2AuthorizationRequestResolver(clientRegistrationRepository),
                        authorizationRequestRepository()),
                new OAuth2LoginAuthenticationFilter(
                        oAuth2AuthenticationSuccessHandler(),
                        authorizationRequestRepository(),
                        oAuth2LoginAuthenticationProvider()),
                new AuthorizationFilter(requestAuthorizationManager())));
    }

    @Bean
    public HttpSessionSecurityContextRepository httpSessionSecurityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.with().role("ADMIN").implies("USER").build();
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
            Member member = memberRepository.findByEmail(username).orElseThrow(() -> new AuthenticationException("존재하지 않는 사용자입니다."));

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

    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(
                httpSessionSecurityContextRepository(),
                memberService);
    }

    @Bean
    public OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        return new OAuth2AuthorizationRequestResolver(clientRegistrationRepository);
    }

    @Bean
    public AuthorizationRequestRepository authorizationRequestRepository() {
        return new AuthorizationRequestRepository();
    }

    public AuthenticationManager oAuth2LoginAuthenticationProvider() {
        return new ProviderManager(List.of(
                new OAuth2LoginAuthenticationProvider(oAuth2UserDetailsServices)));
    }

}
