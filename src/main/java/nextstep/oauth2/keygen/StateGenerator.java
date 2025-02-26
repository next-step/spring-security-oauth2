package nextstep.oauth2.keygen;

import java.security.SecureRandom;
import java.util.Base64;

public class StateGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateState() {
        byte[] randomBytes = new byte[16];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
