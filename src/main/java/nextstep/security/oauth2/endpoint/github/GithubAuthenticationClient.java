package nextstep.security.oauth2.endpoint.github;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(accept = "application/json")
public interface GithubAuthenticationClient {

    @PostExchange("/login/oauth/access_token")
    GithubAccessToken getAccessToken(@RequestParam("client_id") String clientId,
                                     @RequestParam("client_secret") String clientSecret,
                                     @RequestParam("code") String code);
}
