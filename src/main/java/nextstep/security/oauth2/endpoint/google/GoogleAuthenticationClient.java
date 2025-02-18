package nextstep.security.oauth2.endpoint.google;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "https://accounts.google.com/o/oauth2", accept = "application/json")
public interface GoogleAuthenticationClient {

    @PostExchange("/token")
    GoogleAccessToken getAccessToken(@RequestParam("client_id") String clientId,
                                     @RequestParam("client_secret") String clientSecret,
                                     @RequestParam("code") String code,
                                     @RequestParam("redirect_uri") String redirectUri,
                                     @RequestParam("grant_type") String grantType);

}
