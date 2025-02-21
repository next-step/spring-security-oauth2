package nextstep.security.authentication;

public class GoogleUserInfo implements OAuth2UserInfo {
    private final String id;
    private final String email;
    private final String name;
    private final String picture;

    public GoogleUserInfo(String id, String name, String email, String picture) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.picture = picture;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPictureUrl() {
        return picture;
    }
}
