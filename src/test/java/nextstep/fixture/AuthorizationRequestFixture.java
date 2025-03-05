package nextstep.fixture;

import nextstep.security.oauth2.ClientRegistration;
import nextstep.security.oauth2.OAuth2AuthorizationRequest;

public enum AuthorizationRequestFixture {

    GITHUB {
        @Override
        public OAuth2AuthorizationRequest getOAuth2AuthorizationRequest() {
            var registration = new ClientRegistration(
                    "github",
                    "client-id-test",
                    "client-secret-test",
                    "http://localhost:8080/login/oauth2/code",
                    null,
                    null,
                    "https://github.com/login/oauth/authorize",
                    "http://localhost:8089/login/oauth/access_token"
                    , null
            );
            return new OAuth2AuthorizationRequest(registration);
        }
    },
    GOOGLE {
        @Override
        public OAuth2AuthorizationRequest getOAuth2AuthorizationRequest() {
            var registration = new ClientRegistration(
                    "google",
                    "client-id-test",
                    "client-secret-test",
                    "http://localhost:8080/login/oauth2/code",
                    null,
                    null,
                    "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile",
                    "http://localhost:8089/token"
                    , null
            );
            return new OAuth2AuthorizationRequest(registration);
        }
    };

    public abstract OAuth2AuthorizationRequest getOAuth2AuthorizationRequest();
}
