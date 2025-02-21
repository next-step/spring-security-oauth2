package nextstep.oauth2.http;

public class OAuth2User {
    private final String email;
    private final String name;
    private final String picture;

    public OAuth2User(final String email, final String name, final String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }
}
