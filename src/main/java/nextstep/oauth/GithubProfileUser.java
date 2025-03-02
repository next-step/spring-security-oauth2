package nextstep.oauth;

import java.util.Map;

public class GithubProfileUser implements OAuth2ProfileUser {
    private String name;
    private String imageUrl;
    private String email;

    public GithubProfileUser(Map<String, Object> attributes) {
        this.name = attributes.get("name").toString();
        this.imageUrl = attributes.get("avatar_url").toString();
        this.email = attributes.get("email").toString();
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
    public String getEmail() {
        return email;
    }
}
