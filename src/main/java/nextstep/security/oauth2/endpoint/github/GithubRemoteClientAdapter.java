package nextstep.security.oauth2.endpoint.github;

import nextstep.security.oauth2.endpoint.OAuth2AccessToken;
import nextstep.security.oauth2.endpoint.OAuth2RemoteClient;
import nextstep.security.oauth2.user.OAuth2User;

public class GithubRemoteClientAdapter implements OAuth2RemoteClient {

    private final GithubAuthenticationClient githubAuthenticationClient;
    private final GithubApiClient githubApiClient;

    public GithubRemoteClientAdapter(GithubAuthenticationClient githubAuthenticationClient,
                                     GithubApiClient githubApiClient) {

        this.githubAuthenticationClient = githubAuthenticationClient;
        this.githubApiClient = githubApiClient;
    }

    @Override
    public boolean supports(String registrationId) {
        return "github".equals(registrationId);
    }

    @Override
    public OAuth2AccessToken getAccessToken(String clientId,
                                            String clientSecret,
                                            String code,
                                            String redirectUri,
                                            String grantType) {

        return githubAuthenticationClient.getAccessToken(clientId, clientSecret, code);
    }

    @Override
    public OAuth2User getUserInfo(OAuth2AccessToken accessToken) {
        return githubApiClient.getUserInfo("Bearer " + accessToken.getAccessToken());
    }
}
