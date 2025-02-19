package nextstep.security.authentication;

import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public class OAuth2ClientProperties {

  private Map<String, Registration> registrations;
  private Map<String, Provider> providers;

  public Map<String, Registration> getRegistrations() {
    return registrations;
  }

  public void setRegistrations(Map<String, Registration> registrations) {
    this.registrations = registrations;
  }

  public Map<String, Provider> getProviders() {
    return providers;
  }

  public void setProviders(Map<String, Provider> providers) {
    this.providers = providers;
  }

  public static class Registration {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizationGrantType;
    private List<String> scope;

    public String getClientId() {
      return clientId;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public String getClientSecret() {
      return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
      return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
      this.redirectUri = redirectUri;
    }

    public String getAuthorizationGrantType() {
      return authorizationGrantType;
    }

    public void setAuthorizationGrantType(String authorizationGrantType) {
      this.authorizationGrantType = authorizationGrantType;
    }

    public List<String> getScope() {
      return scope;
    }

    public void setScope(List<String> scope) {
      this.scope = scope;
    }
  }

  public static class Provider {

    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String accessTokenUri;

    public String getAuthorizationUri() {
      return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri) {
      this.authorizationUri = authorizationUri;
    }

    public String getTokenUri() {
      return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
      this.tokenUri = tokenUri;
    }

    public String getUserInfoUri() {
      return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri) {
      this.userInfoUri = userInfoUri;
    }

    public String getAccessTokenUri() {
      return accessTokenUri;
    }

    public void setAccessTokenUri(String accessTokenUri) {
      this.accessTokenUri = accessTokenUri;
    }
  }
}
