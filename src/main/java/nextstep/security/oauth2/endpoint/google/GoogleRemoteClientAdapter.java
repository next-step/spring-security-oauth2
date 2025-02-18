package nextstep.security.oauth2.endpoint.google;

import nextstep.security.oauth2.endpoint.OAuth2AccessToken;
import nextstep.security.oauth2.endpoint.OAuth2RemoteClient;
import nextstep.security.oauth2.user.OAuth2User;

public class GoogleRemoteClientAdapter implements OAuth2RemoteClient {
    private final GoogleAuthenticationClient googleAuthenticationClient;
    private final GoogleApiClient googleApiClient;

    public GoogleRemoteClientAdapter(GoogleAuthenticationClient googleAuthenticationClient,
                                     GoogleApiClient googleApiClient) {

        this.googleAuthenticationClient = googleAuthenticationClient;
        this.googleApiClient = googleApiClient;
    }

    @Override
    public boolean supports(String registrationId) {
        return "google".equals(registrationId);
    }

    @Override
    public OAuth2AccessToken getAccessToken(String clientId,
                                            String clientSecret,
                                            String code,
                                            String redirectUri,
                                            String grantType) {

        return googleAuthenticationClient.getAccessToken(clientId,
                                                         clientSecret,
                                                         code,
                                                         redirectUri,
                                                         grantType);
    }

    @Override
    public OAuth2User getUserInfo(OAuth2AccessToken accessToken) {
        return googleApiClient.getUserInfo("Bearer " + accessToken.getAccessToken());
    }
}
