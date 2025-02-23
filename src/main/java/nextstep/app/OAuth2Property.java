package nextstep.app;

import nextstep.security.authentication.OAuth2Client;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

//    public static class OAuth2Client {
//        private String clientId;
//        private String clientSecret;
//        private String tokenUri;
//        private String userInfoUri;
//        private String grantType;
//        private String redirectUri;
//
//        public String getUserInfoUri() {
//            return userInfoUri;
//        }
//
//        public void setUserInfoUri(String userInfoUri) {
//            this.userInfoUri = userInfoUri;
//        }
//
//        public String getTokenUri() {
//            return tokenUri;
//        }
//
//        public void setTokenUri(String tokenUri) {
//            this.tokenUri = tokenUri;
//        }
//
//        public String getClientSecret() {
//            return clientSecret;
//        }
//
//        public void setClientSecret(String clientSecret) {
//            this.clientSecret = clientSecret;
//        }
//
//        public String getClientId() {
//            return clientId;
//        }
//
//        public void setClientId(String clientId) {
//            this.clientId = clientId;
//        }
//
//        public String getRedirectUri() {
//            return redirectUri;
//        }
//
//        public void setRedirectUri(String redirectUri) {
//            this.redirectUri = redirectUri;
//        }
//
//        public String getGrantType() {
//            return grantType;
//        }
//
//        public void setGrantType(String grantType) {
//            this.grantType = grantType;
//        }
//
//        public MultiValueMap<String, String> getParamsForToken(String code) {
//            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//            params.add("code", code);
//            params.add("client_id", clientId);
//            params.add("client_secret", clientSecret);
//            params.add("grant_type", grantType);
//            params.add("redirect_uri", redirectUri);
//            return params;
//        }
//    }

}
