package nextstep.security.oauth2.core;

public enum OAuth2ParameterNames {
    CLIENT_ID("client_id"),
    REDIRECT_URI("redirect_uri"),
    RESPONSE_TYPE("response_type"),
    SCOPE("scope"),
    CODE("code"),
    ERROR("error"),
    ERROR_DESCRIPTION("error_description"),
    ERROR_URI("error_uri"),
    GRANT_TYPE("grant_type"),
    AUTHORIZATION_CODE("authorization_code"),
    ACCESS_TOKEN("access_token");

    private final String value;

    OAuth2ParameterNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
