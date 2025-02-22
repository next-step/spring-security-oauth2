package nextstep.security.oauth2.client.authentication;

import java.util.Map;

public class GoogleProfileUser implements OAuth2ProfileUser {
    private final String name;
    private final String imageUrl;
    private final String email;

    public GoogleProfileUser(Map<String, Object> attributes) {
        this.name = (String) attributes.get("name");
        this.imageUrl = (String) attributes.get("picture");
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
    public String getEmail() {
        return email;
    }
}
