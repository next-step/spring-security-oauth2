package nextstep.security.oauth2.endpoint.github;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = "application/json")
public interface GithubApiClient {

    @GetExchange("/user")
    GithubUser getUserInfo(@RequestHeader("Authorization") String accessToken);
}
