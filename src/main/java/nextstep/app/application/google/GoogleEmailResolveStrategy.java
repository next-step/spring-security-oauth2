package nextstep.app.application.google;

import nextstep.app.application.google.dto.GoogleUserResponse;
import nextstep.security.authentication.oauth.OAuth2EmailResolveStrategy;
import nextstep.security.authentication.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleEmailResolveStrategy implements OAuth2EmailResolveStrategy {
    @Value("${oauth2.google.user.request-uri}")
    private String requestUri;
    @Value("${oauth2.google.registration-id}")
    private String registrationId;

    @Override
    public String getRegistrationId() {
        return registrationId;
    }

    @Override
    public Class<? extends UserResponse> getUserResponseClass() {
        return GoogleUserResponse.class;
    }

    @Override
    public String getRequestUri() {
        return requestUri;
    }
}
