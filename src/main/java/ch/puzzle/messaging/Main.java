package ch.puzzle.messaging;

import ch.puzzle.messaging.artemis.NativeArtemisReceiver;
import ch.puzzle.messaging.artemis.NativeArtemisSender;
import ch.puzzle.messaging.hornetq.NativeHornetQReceiver;
import ch.puzzle.messaging.hornetq.NativeHornetQSender;
import ch.puzzle.messaging.jms.JMSReceiver;
import ch.puzzle.messaging.jms.JMSSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.puzzle.messaging.Configuration.*;

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
            case HORNETQ_JMS:
            case ARTEMIS_JMS:
                processJMS(configuration);
                break;

            case HORNETQ_NATIVE:
                processHornetQNative(configuration);
                break;

            case ARTEMIS_NATIVE:
                processArtemisNative(configuration);
                break;

            default:
                logger.error("Protocol {} not supported", configuration.getProtocol());
        }
    }

    private void processJMS(Configuration configuration) {
        switch (configuration.getMethod()) {
            case Configuration.SEND:
                new JMSSender(configuration).process();
                break;
            case Configuration.RECEIVE:
                new JMSReceiver(configuration).process();
                break;
            default:
                logger.info("Unknown method: {}", configuration.getMethod());
                break;
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

    private void processArtemisNative(Configuration configuration) {
        switch (configuration.getMethod()) {
            case Configuration.SEND:
                new NativeArtemisSender(configuration).process();
                break;
            case Configuration.RECEIVE:
                new NativeArtemisReceiver(configuration).process();
                break;
            default:
                logger.info("Unknown method: {}", configuration.getMethod());
                break;
        }
    }
}
