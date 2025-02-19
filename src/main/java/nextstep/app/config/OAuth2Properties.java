package nextstep.app.config;

public interface OAuth2Properties {
    String getRegistrationId();
    String getClientId();
    String getClientSecret();
    String getScope();
    Authorization getAuthorization();

    interface Authorization {
        String getRequestUri();
        String getRedirectUri();
    }

    interface Token {
        String getRequestUri();
        String getRedirectUri();
    }
}
