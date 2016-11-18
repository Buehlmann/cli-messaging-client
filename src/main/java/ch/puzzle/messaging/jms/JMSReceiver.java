package ch.puzzle.messaging.jms;

import ch.puzzle.messaging.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Created by ben on 18.11.16.
 */
public class JMSReceiver {
    private final Logger logger = LoggerFactory.getLogger(JMSReceiver.class);

    private Configuration configuration;
    private JMSInitializer initializer;

    public JMSReceiver(Configuration configuration) {
        this.configuration = configuration;
        this.initializer = new JMSInitializer(configuration);
    }

    public void process() {
        Session session = null;
        MessageConsumer consumer = null;

        try {
            session = initializer.createJMSSession();
            Queue queue = initializer.createJMSQueue();
            logger.info("Successfully connected to message broker {}", session);

            consumer = session.createConsumer(queue);

            for (int i = 0; i < configuration.getCount(); i++) {
                try {
                    TextMessage message = (TextMessage) consumer.receive();

                    if (i % configuration.getLoginterval() == 0) {
                        logger.info("Received message #{}: {}", i + 1, message);
                    }
                } catch (JMSException e) {
                    logger.error("Error occured while trying to receive message: {}", e.getMessage());
                }
                if (configuration.getSleep() > 0) {
                    try {
                        Thread.currentThread().sleep(configuration.getSleep());
                    } catch (InterruptedException e) {
                    }
                }
            }
        } catch (JMSException e) {
            logger.error("Error occurred while trying to creating producer: {}", e.getMessage());
        } finally {
            try {
                if (consumer != null)
                    consumer.close();
                if (session != null)
                    session.close();
            } catch (JMSException e) {
            }
        }
    }
}
