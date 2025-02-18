package nextstep.app;

import nextstep.security.oauth2.endpoint.OAuth2RemoteClientAdapter;
import nextstep.security.oauth2.endpoint.github.GithubApiClient;
import nextstep.security.oauth2.endpoint.github.GithubAuthenticationClient;
import nextstep.security.oauth2.endpoint.github.GithubRemoteClientAdapter;
import nextstep.security.oauth2.endpoint.google.GoogleApiClient;
import nextstep.security.oauth2.endpoint.google.GoogleAuthenticationClient;
import nextstep.security.oauth2.endpoint.google.GoogleRemoteClientAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@Configuration
public class RemoteClientConfig {

    @Value("${oauth2.url.authentication.github}")
    private String githubAuthenticationUrl;

    @Value("${oauth2.url.authentication.google}")
    private String googleAuthenticationUrl;

    @Value("${oauth2.url.api.github}")
    private String githubApiUrl;

    @Value("${oauth2.url.api.google}")
    private String googleApiUrl;

    @Bean
    public GithubAuthenticationClient githubAuthenticationClient() {
        RestClient restClient = RestClient.builder().baseUrl(githubAuthenticationUrl).build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(restClientAdapter)
                                                                 .build();

        return factory.createClient(GithubAuthenticationClient.class);
    }

    @Bean
    public GithubApiClient githubApiClient() {
        RestClient restClient = RestClient.builder().baseUrl(githubApiUrl).build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(restClientAdapter)
                                                                 .build();

        return factory.createClient(GithubApiClient.class);
    }

    @Bean
    public GoogleAuthenticationClient googleAuthenticationClient() {
        RestClient restClient = RestClient.builder().baseUrl(googleAuthenticationUrl).build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(restClientAdapter)
                                                                 .build();

        return factory.createClient(GoogleAuthenticationClient.class);
    }

    @Bean
    public GoogleApiClient googleApiClient() {
        RestClient restClient = RestClient.builder().baseUrl(googleApiUrl).build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(restClientAdapter)
                                                                 .build();

        return factory.createClient(GoogleApiClient.class);
    }

    @Bean
    public GoogleRemoteClientAdapter googleRemoteClientAdapter(GoogleAuthenticationClient googleAuthenticationClient,
                                                               GoogleApiClient googleApiClient) {
        return new GoogleRemoteClientAdapter(googleAuthenticationClient, googleApiClient);
    }

    @Bean
    public GithubRemoteClientAdapter githubRemoteClientAdapter(GithubAuthenticationClient githubAuthenticationClient,
                                                               GithubApiClient githubApiClient) {
        return new GithubRemoteClientAdapter(githubAuthenticationClient, githubApiClient);
    }

    @Bean
    public OAuth2RemoteClientAdapter oAuthRemoteClientAdapter(GoogleRemoteClientAdapter googleRemoteClientAdapter,
                                                              GithubRemoteClientAdapter githubRemoteClientAdapter) {
        return new OAuth2RemoteClientAdapter(List.of(googleRemoteClientAdapter, githubRemoteClientAdapter));
    }
}
