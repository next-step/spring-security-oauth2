package nextstep.app;

import nextstep.security.authentication.OAuth2Client;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Property {

    private OAuth2Client google;
    private OAuth2Client github;

    public OAuth2Client getGoogle() {
        return google;
    }

    public void setGoogle(OAuth2Client google) {
        this.google = google;
    }

    public OAuth2Client getGithub() {
        return github;
    }

    public void setGithub(OAuth2Client github) {
        this.github = github;
    }
}
