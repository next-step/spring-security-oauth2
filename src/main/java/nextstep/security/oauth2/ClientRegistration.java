package nextstep.security.oauth2;


import jakarta.annotation.Nullable;
import nextstep.security.properties.OAuth2ClientProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ClientRegistration {

    private final String registrationId;

    private final String clientId;

    private final String clientSecret;

    private final String redirectUri;

    private final String responseType;

    private final ProviderDetails providerDetails;

    private final String scope;


    public ClientRegistration(final String registrationId,
                              final String clientId,
                              final String clientSecret,
                              final String redirectUri,
                              final String responseType,
                              final String scope,
                              final String authorizeUri,
                              final String tokenUri,
                              final String userInfoUri
    ) {
        this.registrationId = registrationId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.responseType = responseType;
        this.scope = scope;
        this.redirectUri = redirectUri;
        this.providerDetails = new ProviderDetails(authorizeUri, tokenUri, userInfoUri);
    }

    public static Map<String, ClientRegistration> from(OAuth2ClientProperties oAuth2ClientProperties) {
        var map = new HashMap<String, ClientRegistration>();

        oAuth2ClientProperties.getRegistrations().forEach((key, value) -> {
                    map.put(key,
                            new ClientRegistration(
                                    value.getRegistrationId(),
                                    value.getClientId(),
                                    value.getClientSecret(),
                                    value.getRedirectUri(),
                                    value.getResponseType(),
                                    value.getScope(),
                                    value.getAuthorizeUri(),
                                    value.getTokenUri(),
                                    value.getUserUri()
                            ));
                }
        );

        return map;
    }

    @Nullable
    public String getGrantType() {
        if (responseType == null) {
            return null;
        }
        if (this.responseType.equals("code")) {
            return "authorization_code";
        }
        return null;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getTokenUri() {
        return this.providerDetails.tokenUri;
    }

    public String getAuthorizeUri() {
        return this.providerDetails.authorizationUri;
    }

    public String getScope() {
        return this.scope;
    }

    public String getUserUri() {
        return this.providerDetails.userInfoEndpoint.uri;
    }


    public static class ProviderDetails implements Serializable {
        private final String authorizationUri;

        private final String tokenUri;

        private final UserInfoEndpoint userInfoEndpoint;

        public ProviderDetails(final String authorizationUri,
                               final String tokenUri,
                               final String userInfoUri) {
            this.authorizationUri = authorizationUri;
            this.tokenUri = tokenUri;
            this.userInfoEndpoint = new UserInfoEndpoint(userInfoUri);
        }

        public String getAuthorizationUri() {
            return this.authorizationUri;
        }

        public String getTokenUri() {
            return this.tokenUri;
        }


        public UserInfoEndpoint getUserInfoEndpoint() {
            return this.userInfoEndpoint;
        }


        public record UserInfoEndpoint(String uri) implements Serializable {

        }

    }
}
