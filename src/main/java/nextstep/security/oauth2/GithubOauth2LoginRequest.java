package nextstep.security.oauth2;

public record GithubOauth2LoginRequest(
        String clientId,
        String responseType,
        String scope,
        String redirectUri
) {
}
