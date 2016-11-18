package ch.puzzle.messaging;

import ch.puzzle.messaging.hornetq.JMSHornetQReceiver;
import ch.puzzle.messaging.hornetq.JMSHornetQSender;
import ch.puzzle.messaging.hornetq.NativeHornetQReceiver;
import ch.puzzle.messaging.hornetq.NativeHornetQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.puzzle.messaging.Configuration.HORNETQ_NATIVE;
import static ch.puzzle.messaging.Configuration.HORNETQ_JMS;

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
            case HORNETQ_NATIVE:
                processHornetQNative(configuration);
                break;

            case HORNETQ_JMS:
                processHornetQJMS(configuration);
                break;

            default:
                logger.error("Protocol {} not supported", configuration.getProtocol());
        }
    }

    private void processHornetQNative(Configuration configuration) {
        switch (configuration.getMethod()) {

            case Configuration.SEND:
                new NativeHornetQSender(configuration).process();
                break;

            case Configuration.RECEIVE:
                new NativeHornetQReceiver(configuration).process();
                break;

            default:
                logger.info("Unknown method: {}", configuration.getMethod());
                break;
        }
    }

    private void processHornetQJMS(Configuration configuration) {
        switch (configuration.getMethod()) {

            case Configuration.SEND:
                new JMSHornetQSender(configuration).process();
                break;

            case Configuration.RECEIVE:
                new JMSHornetQReceiver(configuration).process();
                break;

            default:
                logger.info("Unknown method: {}", configuration.getMethod());
                break;
        }
    }
}
