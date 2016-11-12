package ch.puzzle.messaging;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by ben on 12.11.16.
 */
class PayloadGenerator {
    private Random r = new SecureRandom();

    String generatePayload(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append((char) (97 + r.nextInt(26)));
        }
        return sb.toString();
    }
}
