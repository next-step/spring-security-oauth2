package nextstep.security.authentication;

import java.io.Serial;

public class UnsupportedOAuth2ProviderException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1;

    public UnsupportedOAuth2ProviderException(String provider) {
        super("Unsupported OAuth2ProviderException provider: " + provider);
    }
}
