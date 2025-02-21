package nextstep.oauth2.keygen;

import java.security.SecureRandom;


public final class DefaultStateGenerator implements StateGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int LENGTH = 8;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private DefaultStateGenerator() {}

    public static DefaultStateGenerator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public String generateKey() {
        final StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHABET.charAt(
                    RANDOM.nextInt(ALPHABET.length())
            ));
        }
        return sb.toString();
    }

    public static class SingletonHolder {
        private static final DefaultStateGenerator INSTANCE = new DefaultStateGenerator();
    }
}
