package nextstep.app.config;

import nextstep.security.access.AuthorizeRequestMatcherRegistry;
import nextstep.security.access.matcher.AnyRequestMatcher;
import nextstep.security.access.matcher.MvcRequestMatcher;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.security.authentication.UsernamePasswordAuthenticationProvider;
import nextstep.security.authorization.AuthorizationFilter;
import nextstep.security.authentication.Oauth2LoginAuthenticationFilter;
import nextstep.security.authorization.Oauth2AuthorizationRequestRedirectFilter;
import nextstep.security.authorization.SecurityContextHolderFilter;
import nextstep.security.authorization.manager.RequestAuthorizationManager;
import nextstep.security.config.DefaultSecurityFilterChain;
import nextstep.security.config.FilterChainProxy;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContextRepository;
import nextstep.security.exception.ExceptionTranslateFilter;
import nextstep.security.savedRequest.HttpSessionRequestCache;
import nextstep.security.savedRequest.RequestCache;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;

@Configuration
public class AuthConfig implements WebMvcConfigurer {

    private static final String OAUTH2_REDIRECT_URL = "https://github.com/login/oauth/authorize?response_type=code&client_id=7fc956935c0618c560da&scope=read:user&redirect_uri=http://localhost:8080/oauth2/access";

    private final UserDetailsService userDetailsService;

    public AuthConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public DelegatingFilterProxy securityFilterChainProxy() {
        return new DelegatingFilterProxy("filterChainProxy");
    }

    @Bean
    public FilterChainProxy filterChainProxy(SecurityFilterChain securityFilterChain) {
        return new FilterChainProxy(List.of(securityFilterChain));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(WebClient webClient) {
        List<Filter> filters = List.of(
            new SecurityContextHolderFilter(securityContextRepository()),
            new UsernamePasswordAuthenticationFilter(authenticationManager(), securityContextRepository()),
            new BasicAuthenticationFilter(authenticationManager()),
            new ExceptionTranslateFilter(requestCache()),
            new Oauth2AuthorizationRequestRedirectFilter(new MvcRequestMatcher(HttpMethod.GET, "/oauth2/authorization/github"),OAUTH2_REDIRECT_URL),
            new Oauth2LoginAuthenticationFilter(new MvcRequestMatcher(HttpMethod.GET, "/oauth2/access"), webClient, securityContextRepository()),
            new AuthorizationFilter(authorizationManager())
        );
        return new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE, filters);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public RequestCache requestCache() {
        return new HttpSessionRequestCache();
    }

    @Bean
    public RequestAuthorizationManager authorizationManager() {
        AuthorizeRequestMatcherRegistry requestMatcherRegistry = new AuthorizeRequestMatcherRegistry();
        requestMatcherRegistry
                .matcher(new MvcRequestMatcher(HttpMethod.GET, "/members")).hasAuthority("ADMIN")
                .matcher(new MvcRequestMatcher(HttpMethod.GET, "/members/me")).authenticated()
                .matcher(new MvcRequestMatcher(HttpMethod.GET, "/members/authentication")).authenticated();
        return new RequestAuthorizationManager(requestMatcherRegistry);
    }

    @Bean
    public RequestAuthorizationManager oauth2authorizationManager() {
        AuthorizeRequestMatcherRegistry requestMatcherRegistry = new AuthorizeRequestMatcherRegistry();
        requestMatcherRegistry
            .matcher(new MvcRequestMatcher(HttpMethod.GET, "/oauth2/authorization/github"));
        return new RequestAuthorizationManager(requestMatcherRegistry);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new AuthenticationManager(new UsernamePasswordAuthenticationProvider(userDetailsService));
    }

}
