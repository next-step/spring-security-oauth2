package nextstep.app.application.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.authentication.UserResponse;

public record GithubUserResponse(
        @JsonProperty("avatar_url")
        String avatarUrl,
        @JsonProperty("name")
        String name,
        @JsonProperty("email")
        String email
) implements UserResponse {
    @Override
    public String getEmail() {
        return email;
    }
}
