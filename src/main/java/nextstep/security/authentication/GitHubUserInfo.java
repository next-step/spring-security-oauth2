package nextstep.security.authentication;

public class GitHubUserInfo implements OAuth2UserInfo {
    private final String id;
    private final String name;
    private final String email;
    private final String avatar_url;

    public GitHubUserInfo(String id, String name, String email, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar_url = avatarUrl;
    }

    // Getter, Setter
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
        return avatar_url;
    }
}
