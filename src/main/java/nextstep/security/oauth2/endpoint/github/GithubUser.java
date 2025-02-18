package nextstep.security.oauth2.endpoint.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.oauth2.user.OAuth2User;

import java.util.List;
import java.util.Set;

public class GithubUser implements OAuth2User {

    private String email;
    private String name;

    private Set<String> roles = Set.of("USER");

    @JsonProperty("avatar_url")
    private String imageUrl;

    public GithubUser() {
    }

    @Override
    public String getEmail() {
        return email;
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
    public Set<String> getRoles() {
        return Set.copyOf(roles);
    }
}
