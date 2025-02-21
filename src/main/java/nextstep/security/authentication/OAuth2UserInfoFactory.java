package nextstep.security.authentication;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String providerName, Map<String, Object> attributes) {
        if ("google".equalsIgnoreCase(providerName)) {
            return new GoogleUserInfo(
                    (String) attributes.get("id"),
                    (String) attributes.get("name"),
                    (String) attributes.get("email"),
                    (String) attributes.get("picture")
            );
        } else if ("github".equalsIgnoreCase(providerName)) {
            return new GitHubUserInfo(
                    (String) attributes.get("id"),
                    (String) attributes.get("name"),
                    (String) attributes.get("email"),
                    (String) attributes.get("avatar_url")
            );
        }
        throw new IllegalArgumentException("Unsupported provider: " + providerName);
    }
}
