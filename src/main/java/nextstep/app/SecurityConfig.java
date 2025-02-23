package nextstep.app;

import nextstep.app.domain.MemberRepository;
import nextstep.app.domain.MemberService;
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
import nextstep.security.context.SecurityContextHolderFilter;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EnableAspectJAutoProxy
@Configuration
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final OAuth2Property oAuth2Property;

    public SecurityConfig(MemberRepository memberRepository, OAuth2Property oAuth2Property) {
        this.memberRepository = memberRepository;
        this.oAuth2Property = oAuth2Property;
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
    public SecuredMethodInterceptor securedMethodInterceptor() {
        return new SecuredMethodInterceptor();
    }

    @Bean
    public SecurityFilterChain securityFilterChain() {
        return new DefaultSecurityFilterChain(
                List.of(
                        new SecurityContextHolderFilter(),
                        new GithubLoginRedirectFilter(),
                        new GoogleLoginRedirectFilter(),
                        new OAuth2AuthenticationFilter(userDetailsService(), oAuth2ClientFactory()),
                        new UsernamePasswordAuthenticationFilter(userDetailsService()),
                        new BasicAuthenticationFilter(userDetailsService()),
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
    public OAuth2ClientFactory oAuth2ClientFactory() {
        return new OAuth2ClientFactory(
                Map.of(
                        new MvcRequestMatcher(HttpMethod.GET, "/oauth2/authorization/google"), oAuth2Property.getGoogle(),
                        new MvcRequestMatcher(HttpMethod.GET, "/oauth2/authorization/github"), oAuth2Property.getGithub(),
                        new MvcRequestMatcher(HttpMethod.GET, "/login/oauth2/code/google"), oAuth2Property.getGoogle(),
                        new MvcRequestMatcher(HttpMethod.GET, "/login/oauth2/code/github"), oAuth2Property.getGithub()
                ));
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new MemberService(memberRepository);
    }
}
