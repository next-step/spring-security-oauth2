package nextstep.security.oauth2.endpoint.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.oauth2.endpoint.OAuth2AccessToken;

import java.util.HashSet;
import java.util.Set;

public class GoogleAccessToken implements OAuth2AccessToken {
    @JsonProperty("access_token")
    private String accessToken;

    private String scope;

    public GoogleAccessToken() {
    }

    public GoogleAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public Set<String> getScopes() {
        Set<String> scopes = new HashSet<>();
        if (scope == null) {
            return scopes;
        }

        for (String scope : scope.split(" ")) {
            scopes.add(scope.trim());
        }
        return scopes;
    }
}
