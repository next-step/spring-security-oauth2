package nextstep.app.config;

import nextstep.app.application.OAuth2EmailStrategyResolver;
import nextstep.app.application.OAuth2TokenStrategyRequester;
import nextstep.security.access.AnyRequestMatcher;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcherEntry;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.access.hierarchicalroles.RoleHierarchyImpl;
import nextstep.security.authentication.filter.BasicAuthenticationFilter;
import nextstep.security.authentication.filter.UsernamePasswordAuthenticationFilter;
import nextstep.security.authentication.filter.oauth.OAuth2LoginAuthenticationFilter;
import nextstep.security.authentication.filter.oauth.OAuth2RedirectAuthenticationFilter;
import nextstep.security.authentication.oauth.OAuth2AuthenticationRequestResolver;
import nextstep.security.authorization.*;
import nextstep.security.config.DefaultSecurityFilterChain;
import nextstep.security.config.DelegatingFilterProxy;
import nextstep.security.config.FilterChainProxy;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.context.SecurityContextHolderFilter;
import nextstep.security.userservice.OAuth2UserService;
import nextstep.security.userservice.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@EnableAspectJAutoProxy
@Configuration
public class SecurityConfig {

    private final OAuth2AuthenticationRequestResolver requestResolver;
    private final OAuth2TokenStrategyRequester oAuth2TokenStrategyRequester;
    private final OAuth2EmailStrategyResolver oAuth2EmailStrategyResolver;
    private final UserDetailsService userDetailsService;
    private final OAuth2UserService auth2UserService;

    public SecurityConfig(
            OAuth2AuthenticationRequestResolver requestResolver,
            OAuth2TokenStrategyRequester oAuth2TokenStrategyRequester,
            OAuth2EmailStrategyResolver oAuth2EmailStrategyResolver,
            UserDetailsService userDetailsService,
            OAuth2UserService auth2UserService
    ) {
        this.requestResolver = requestResolver;
        this.oAuth2TokenStrategyRequester = oAuth2TokenStrategyRequester;
        this.oAuth2EmailStrategyResolver = oAuth2EmailStrategyResolver;
        this.userDetailsService = userDetailsService;
        this.auth2UserService = auth2UserService;
    }

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy(
            SecurityFilterChain securityFilterChain
    ) {
        return new DelegatingFilterProxy(filterChainProxy(List.of(securityFilterChain)));
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
        final var oAuth2LoginAuthenticationFilter = new OAuth2LoginAuthenticationFilter(
                oAuth2TokenStrategyRequester,
                oAuth2EmailStrategyResolver,
                auth2UserService
        );

        return new DefaultSecurityFilterChain(
                List.of(
                        new SecurityContextHolderFilter(),
                        new OAuth2RedirectAuthenticationFilter(requestResolver, auth2UserService),
                        oAuth2LoginAuthenticationFilter,
                        new UsernamePasswordAuthenticationFilter(userDetailsService),
                        new BasicAuthenticationFilter(userDetailsService),
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
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/members"), new AuthorityAuthorizationManager<>(roleHierarchy(), "ADMIN")));
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/members/me"), new AuthorityAuthorizationManager<>(roleHierarchy(), "USER")));
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/search"), new PermitAllAuthorizationManager<>()));
        mappings.add(new RequestMatcherEntry<>(AnyRequestMatcher.INSTANCE, new PermitAllAuthorizationManager<>()));
        return new RequestAuthorizationManager(mappings);
    }
}
