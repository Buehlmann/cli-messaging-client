package ch.puzzle.messaging.hornetq;

import ch.puzzle.messaging.Configuration;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ben on 12.11.16.
 */
public class HornetQReceiver {
    private final Logger logger = LoggerFactory.getLogger(HornetQReceiver.class);

    private Configuration configuration;
    private HornetQInitializer initializer;

    public HornetQReceiver(Configuration configuration) {
        this.configuration = configuration;
        this.initializer = new HornetQInitializer();
    }

    public void process() {
        ClientSession session = null;
        ClientConsumer consumer = null;

        try {
            session = initializer.createSession(configuration);
            if (session == null) {
                logger.error("Could not create client session");
                return;
            }
            session.start();
            consumer = session.createConsumer(configuration.getDestination());
            logger.info("Successfully connected to message broker {}", session);
            logger.info("Waiting for messages...");

            for (int i = 0; i < configuration.getCount(); i++) {
                try {
                    ClientMessage message = consumer.receive();

                    if (configuration.isVerbose()) {
                        logger.info("Received message #{}: {}, body length: {}", i + 1, message, message.getBodySize());
                    } else {
                        if (i % 100 == 0) {
                            logger.info("Received message #{}", i + 1);
                        }
                    }
                } catch (HornetQException e) {
                    logger.error("Error occured while receiving a message: {}", e.getMessage());
                }

                if (configuration.getSleep() > 0) {
                    Thread.currentThread().sleep(configuration.getSleep());
                }
            }

        } catch (HornetQException e) {
            logger.error("Error occurred while trying to connect to broker: {}", e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Error while sleeping...", e);
        } finally {
            try {
                if (consumer != null)
                    consumer.close();
                if (session != null) {
                    session.stop();
                    session.close();
                }
            } catch (HornetQException e) {
            }
        }
    }
}
