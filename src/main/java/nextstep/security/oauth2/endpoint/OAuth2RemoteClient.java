package nextstep.security.oauth2.endpoint;

import nextstep.security.oauth2.user.OAuth2User;

public interface OAuth2RemoteClient {
    boolean supports(String registrationId);

    OAuth2AccessToken getAccessToken(String clientId,
                                     String clientSecret,
                                     String code,
                                     String redirectUri, String grantType);

    OAuth2User getUserInfo(OAuth2AccessToken accessToken);
}


