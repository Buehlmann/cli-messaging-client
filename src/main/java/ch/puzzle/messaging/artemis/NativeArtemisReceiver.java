package ch.puzzle.messaging.artemis;

import ch.puzzle.messaging.Configuration;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ben on 18.11.16.
 */
public class NativeArtemisReceiver {
    private final Logger logger = LoggerFactory.getLogger(NativeArtemisReceiver.class);

    private Configuration configuration;
    private ArtemisInitializer initializer;

    public NativeArtemisReceiver(Configuration configuration) {
        this.configuration = configuration;
        this.initializer = new ArtemisInitializer(configuration);
    }

    public void process() {
        ClientSession session = null;
        ClientConsumer consumer = null;

        try {
            session = initializer.createNativeSession();
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
                    if (consumer.isClosed()) {
                        logger.error("Consumer is closed - exiting. Failover?");
                        return;
                    }
                    message.acknowledge();
                    if (i % configuration.getLoginterval() == 0) {
                        if (!configuration.isPrintMessageBody()) {
                            logger.info("Received message #{}: {}, body size in bytes: {}", i + 1, message, message.getBodySize());                            
                        } else {                            
                            logger.info("Received message #{}: {}, body size in bytes: {}, message body: \"{}\"", i + 1, message, message.getBodySize(), message.getBodyBuffer().readString());
                        }
                    }
                } catch (ActiveMQException e) {
                    logger.error("Error occured while receiving a message: {}", e.getMessage());
                }

                if (configuration.getSleep() > 0) {
                    Thread.currentThread().sleep(configuration.getSleep());
                }
            }

        } catch (ActiveMQException e) {
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
            } catch (ActiveMQException e) {
            }
        }
    }
}
