package nextstep.security.oauth2.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GithubUser {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("login")
    private String loginId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("avatar_url")
    private String imageUrl;

    @JsonProperty("email")
    private String email;

    public GithubUser() { }

    public GithubUser(Long id, String loginId, String name, String imageUrl, String email) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
