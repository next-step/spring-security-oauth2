package nextstep.security.oauth2.endpoint.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.oauth2.user.OAuth2User;

import java.util.Set;

public class GoogleUser implements OAuth2User {

    private String email;
    private String name;

    @JsonProperty("picture")
    private String imageUrl;

    private Set<String> roles = Set.of("USER");

    public GoogleUser() {
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
