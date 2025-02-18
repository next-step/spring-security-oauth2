package nextstep.security.oauth2.endpoint;

import java.util.Set;

public interface OAuth2AccessToken {
    String getAccessToken();

    Set<String> getScopes();
}
