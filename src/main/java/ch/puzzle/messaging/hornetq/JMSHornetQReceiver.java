package ch.puzzle.messaging.hornetq;

import ch.puzzle.messaging.Configuration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Created by ben on 18.11.16.
 */
public class JMSHornetQReceiver {
    private final Logger logger = LoggerFactory.getLogger(JMSHornetQReceiver.class);

    private Configuration configuration;
    private HornetQInitializer initializer;

    public JMSHornetQReceiver(Configuration configuration) {
        this.configuration = configuration;
        this.initializer = new HornetQInitializer();
    }

    public void process() {
        Session session = null;
        MessageConsumer consumer = null;

        try {
            session = initializer.createJMSSession(configuration);
            logger.info("Successfully connected to message broker {}", session);

            Queue queue = HornetQJMSClient.createQueue(configuration.getDestination());
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
