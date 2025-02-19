package nextstep.security.authentication.oauth;

import nextstep.security.userservice.OAuth2ClientRegistration;

public interface OAuth2AuthenticationRequestResolver {
    OAuth2AuthorizationRequest resolve(OAuth2ClientRegistration oAuth2ClientRegistration);
}
