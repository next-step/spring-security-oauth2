package nextstep.oauth2.profile;

import java.util.Map;

public record GithubProfileUser(
        String name,
        String imageUrl,
        String email
) implements OAuth2ProfileUser {
    public static GithubProfileUser of(Map<String, Object> attributes) {
        return new GithubProfileUser(
                attributes.get("name").toString(),
                attributes.get("avatar_url").toString(),
                attributes.get("email").toString()
        );
    }
}
