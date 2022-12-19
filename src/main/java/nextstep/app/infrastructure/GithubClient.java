package nextstep.app.infrastructure;

import nextstep.app.infrastructure.dto.AccessRequest;
import nextstep.app.infrastructure.dto.AccessResponse;
import nextstep.app.infrastructure.dto.UserResponse;
import nextstep.security.oauth2user.BaseOauth2User;
import nextstep.security.oauth2user.OAuth2User;
import nextstep.security.oauth2user.Oauth2UserService;
import nextstep.security.exception.AuthenticationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;


@Component
public class GithubClient implements Oauth2UserService {

    private final WebClient webClient;

    public GithubClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public OAuth2User loadUser(String accessToken) throws AuthenticationException {
        final AccessResponse accessResponse = webClient.post()
            .uri("https://github.com/login/oauth/access_token")
            .accept(MediaType.APPLICATION_JSON)
            .body(
                BodyInserters.fromValue(new AccessRequest(accessToken))
            )
            .retrieve()
            .bodyToMono(AccessResponse.class)
            .block();

        final UserResponse userResponse = webClient.get()
            .uri("https://api.github.com/user")
            .header("Authorization", "Bearer " + accessResponse.getAccessToken())
            .retrieve()
            .bodyToMono(UserResponse.class)
            .block();
        return new BaseOauth2User(userResponse.getId(), Set.of());
    }
}
