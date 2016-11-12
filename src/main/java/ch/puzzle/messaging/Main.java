package ch.puzzle.messaging;

import ch.puzzle.messaging.hornetq.HornetQReceiver;
import ch.puzzle.messaging.hornetq.HornetQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.puzzle.messaging.Configuration.HORNETQ;

/**
 * Created by ben on 11.11.16.
 */
public class Main {
    private final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        new Main().start(args);
    }

    private void start(String[] args) {
        Configuration configuration = new Configuration(args);

        switch (configuration.getProtocol()) {
            case HORNETQ:
                processHornetQ(configuration);
                break;

            default:
                logger.error("Protocol {} not supported", configuration.getProtocol());
        }
    }

    private void processHornetQ(Configuration configuration) {
        switch (configuration.getMethod()) {

            case Configuration.SEND:
                new HornetQSender(configuration).process();
                break;

            case Configuration.RECEIVE:
                new HornetQReceiver(configuration).process();
                break;

            default:
                logger.info("Unknown method: {}", configuration.getMethod());
                break;
        }
    }
}
