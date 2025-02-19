package nextstep.app.application.github;

import nextstep.app.application.github.dto.GithubUserResponse;
import nextstep.security.authentication.oauth.OAuth2EmailResolveStrategy;
import nextstep.security.authentication.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GithubEmailResolveStrategy implements OAuth2EmailResolveStrategy {
    @Value("${oauth2.github.user.request-uri}")
    private String requestUri;

    @Override
    public String getOAuth2Type() {
        return "github";
    }

    @Override
    public Class<? extends UserResponse> getUserResponseClass() {
        return GithubUserResponse.class;
    }

    @Override
    public String getRequestUri() {
        return requestUri;
    }
}
