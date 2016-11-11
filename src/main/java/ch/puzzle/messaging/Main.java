package ch.puzzle.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ben on 11.11.16.
 */
public class Main {
    private final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        new Main().start(args);
    }

    void start(String[] args) {
        Configuration configuration = new Configuration(args);

        switch(configuration.getMethod()) {

            case Configuration.SEND:
                new HornetQSender(configuration).process();
                break;

            case Configuration.RECEIVE:
                // TODO:
                break;

            default:
                logger.info("Unknown method: {}", configuration.getMethod());
                break;
        }
    }
}
