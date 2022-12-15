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
    private String image_url;

    @JsonProperty("email")
    private String email;

    public GithubUser() { }

    public GithubUser(Long id, String loginId, String name, String image_url, String email) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.image_url = image_url;
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

    public String getImage_url() {
        return image_url;
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

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
