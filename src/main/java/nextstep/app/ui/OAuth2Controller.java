package nextstep.app.ui;

import nextstep.app.application.OAuth2TokenRequester;
import nextstep.app.application.dto.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OAuth2Controller {

    private static final Logger log = LoggerFactory.getLogger(OAuth2Controller.class);
    private final OAuth2TokenRequester requester;

    public OAuth2Controller(OAuth2TokenRequester requester) {
        this.requester = requester;
    }

    @GetMapping("/login/oauth2/code/google")
    public String googleCallback(@RequestParam String code) {
        TokenResponse response = requester.request(code);
        log.debug("Received token response: " + response);
        return "redirect:/";
    }
}
