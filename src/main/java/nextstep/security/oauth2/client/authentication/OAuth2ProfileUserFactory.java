package nextstep.security.oauth2.client.authentication;

import java.util.Map;

public class OAuth2ProfileUserFactory {
    public static OAuth2ProfileUser create(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "github" -> new GithubProfileUser(attributes);
            case "google" -> new GoogleProfileUser(attributes);
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        };
    }
}
