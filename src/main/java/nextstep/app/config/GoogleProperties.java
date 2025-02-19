package nextstep.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "oauth2.google")
public class GoogleProperties implements OAuth2Properties {

    private String registrationId;
    private String clientId;
    private String clientSecret;
    private Authorization authorization = new Authorization();
    private Token token = new Token();

    public static class Authorization implements OAuth2Properties.Authorization{
        private String requestUri;
        private String redirectUri;

        public String getRequestUri() {
            return requestUri;
        }

        public void setRequestUri(String requestUri) {
            this.requestUri = requestUri;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }
    }

    public static class Token implements OAuth2Properties.Token {
        private String requestUri;
        private String redirectUri;

        public String getRequestUri() {
            return requestUri;
        }

        public void setRequestUri(String requestUri) {
            this.requestUri = requestUri;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getScope() {
        return "email profile";
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
