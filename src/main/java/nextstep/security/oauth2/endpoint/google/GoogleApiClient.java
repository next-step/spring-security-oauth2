package nextstep.security.oauth2.endpoint.google;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "https://www.googleapis.com", accept = "application/json")
public interface GoogleApiClient {

    @GetExchange("/oauth2/v1/userinfo")
    GoogleUser getUserInfo(@RequestHeader("Authorization") String accessToken);

}
