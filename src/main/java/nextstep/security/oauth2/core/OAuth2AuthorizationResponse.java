package nextstep.security.oauth2.core;

import org.springframework.util.Assert;

public record OAuth2AuthorizationResponse(
        String code,
        String redirectUri
) {
    public static OAuth2AuthorizationResponse success(String code, String redirectUri) {
        return new OAuth2AuthorizationResponse(code, redirectUri);
    }

    public static OAuth2AuthorizationResponse error(String errorCode, String redirectUri) {
        Assert.hasText(errorCode, "errorCode cannot be empty");
        return new OAuth2AuthorizationResponse(errorCode, redirectUri);
    }
}
