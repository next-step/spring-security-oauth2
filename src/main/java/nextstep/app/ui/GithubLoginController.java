package nextstep.app.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class GithubLoginController {
    @GetMapping("/oauth2/authorization/github")
    public ResponseEntity<Boolean> authorizationOauth() {
        return ResponseEntity.ok(true);
    }

    @GetMapping("/login/oauth2/code/github")
    public ResponseEntity<Boolean> loginOAuth() {
        return ResponseEntity.ok(true);
    }
}
