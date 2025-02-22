package nextstep.app.config;

import java.util.Set;

public interface OAuth2Properties {
    String getRegistrationId();
    String getClientId();
    String getClientSecret();
    Set<String> getScope();
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
