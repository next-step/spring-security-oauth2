package nextstep.oauth2.web;

import java.security.SecureRandom;

public final class StateGenerator {
    private static final SecureRandom random = new SecureRandom();

    private StateGenerator() {}

    public static String generateKey() {
        final int length = 8;
        final String alphabet = "abcdefghijklmnopqrstuvwxyz";
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }
}
