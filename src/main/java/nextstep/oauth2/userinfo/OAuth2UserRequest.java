package nextstep.oauth2.userinfo;

import nextstep.oauth2.authentication.OAuth2AccessToken;
import nextstep.oauth2.registration.ClientRegistration;
import org.springframework.util.StringUtils;

public class OAuth2UserRequest {
    private final ClientRegistration clientRegistration;
    private final OAuth2AccessToken accessToken;

    public OAuth2UserRequest(final ClientRegistration clientRegistration, final OAuth2AccessToken accessToken) {
        this.clientRegistration = clientRegistration;
        this.accessToken = accessToken;
    }

    public boolean isNotUserInfoUri() {
        return !StringUtils
                .hasText(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri());
    }

    public String userInfoUri() {
        return clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();
    }

    public boolean isNotUserNameAttributeName() {
        return !StringUtils
                .hasText(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
    }

    public String userNameAttributeName() {
        return clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
    }

    public OAuth2AccessToken accessToken() {
        return accessToken;
    }

    public String registrationId() {
        return clientRegistration.getRegistrationId();
    }
}
