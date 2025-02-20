package nextstep.security.oauth2.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public final class StateGenerator {

    private static final int DEFAULT_LENGTH = 16;

    public StateGenerator() {
    }

    public static String generateState() {
        Random random = new SecureRandom();
        byte[] bytes = new byte[DEFAULT_LENGTH];
        random.nextBytes(bytes);

        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }
}
