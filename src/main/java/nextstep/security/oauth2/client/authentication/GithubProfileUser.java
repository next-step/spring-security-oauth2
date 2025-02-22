package nextstep.security.oauth2.client.authentication;

import java.util.Map;

public class GithubProfileUser implements OAuth2ProfileUser {
    private final String name;
    private final String imageUrl;
    private final String email;

    public GithubProfileUser(Map<String, Object> attributes) {
        this.name = (String) attributes.get("name");
        this.imageUrl = (String) attributes.get("avatar_url");
        this.email = (String) attributes.get("email"); // 이메일이 null일 수도 있음
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
