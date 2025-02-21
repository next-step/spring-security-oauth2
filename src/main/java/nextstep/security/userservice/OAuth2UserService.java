package nextstep.security.userservice;

import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;
import nextstep.security.authentication.oauth.OAuth2User;

public interface OAuth2UserService {
    OAuth2ClientRegistration loadClientRegistrationByRegistrationId(String registrationId);
    void saveOAuth2AuthorizationRequest(OAuth2AuthorizationRequest oAuth2AuthorizationRequest);
    OAuth2AuthorizationRequest consumeOAuth2AuthorizationRequest(String state);

    OAuth2User loadUserBy(String username);
}
