package nextstep.security.oauth2.client.authentication;

import jakarta.annotation.Nullable;
import java.util.Map;

public class GithubProfileUser implements OAuth2ProfileUser {
    private final String name;
    private final String imageUrl;
    @Nullable
    private final String email;

    public GithubProfileUser(Map<String, Object> attributes) {
        this.name = (String) attributes.get("name");
        this.imageUrl = (String) attributes.get("avatar_url");
        this.email = (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    @Nullable
    public String getEmail() {
        return email;
    }
}
