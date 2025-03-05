package nextstep.security.oauth2;

import org.springframework.lang.NonNull;
import org.springframework.web.util.UriComponentsBuilder;

public class OAuth2AuthorizationRequest {

    private final ClientRegistration registration;

    public OAuth2AuthorizationRequest(ClientRegistration registration) {
        this.registration = registration;
    }

    @NonNull
    public String getRedirectPath() {
        return UriComponentsBuilder.fromHttpUrl(registration.getAuthorizeUri())
                .queryParam(OAuth2Parameter.CLIENT_ID.getPath(), registration.getClientId())
                .queryParam(OAuth2Parameter.RESPONSE_TYPE.getPath(), OAuth2Parameter.RESPONSE_TYPE.getDefaultValue())
                .queryParam(OAuth2Parameter.SCOPE.getPath(), registration.getScope())
                .queryParam(OAuth2Parameter.REDIRECT_URL.getPath(), registration.getRedirectUri())
                .toUriString();
    }

    public ClientRegistration getRegistration() {
        return registration;
    }
}
