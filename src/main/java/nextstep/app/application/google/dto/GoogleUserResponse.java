package nextstep.app.application.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.authentication.UserResponse;

public record GoogleUserResponse(
        String email,
        @JsonProperty("verified_email")
        boolean emailVerified,
        String name,
        String picture
) implements UserResponse {
    @Override
    public String getEmail() {
        return email;
    }
}
