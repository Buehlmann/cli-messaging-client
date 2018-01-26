package ch.puzzle.messaging.hornetq;

import ch.puzzle.messaging.Configuration;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ben on 11.11.16.
 */
public class NativeHornetQSender {
    private final Logger logger = LoggerFactory.getLogger(NativeHornetQSender.class);

    private Configuration configuration;
    private HornetQInitializer initializer;

    public NativeHornetQSender(Configuration configuration) {
        this.configuration = configuration;
        this.initializer = new HornetQInitializer(configuration);
    }

    public void process() {
        ClientSession session = null;
        ClientProducer producer = null;

        try {
            session = initializer.createNativeSession();
            if (session == null) {
                logger.error("Could not create client session");
                System.exit(1);
            }
            producer = session.createProducer(configuration.getDestination());

            for (int i = 0; i < configuration.getCount(); i++) {
                try {
                    ClientMessage message = session.createMessage(true);
                    message.getBodyBuffer().writeString(configuration.getPayload());
                    if (producer.isClosed()) {
                        logger.error("Producer is closed - exiting. Failover?");
                        System.exit(1);
                    }
                    producer.send(message);

                    if (i % configuration.getLoginterval() == 0) {
                        logger.info("Sent message #{}: {}", i + 1, message);
                    }
                } catch (HornetQException e) {
                    logger.error("Error occured while trying to send message: {}", e.getMessage());
                    System.exit(1);                    
                }

                if (configuration.getSleep() > 0) {
                    Thread.currentThread().sleep(configuration.getSleep());
                }
            }

        } catch (HornetQException e) {
            logger.error("Error occurred while trying to connect to broker: {}", e.getMessage());
            System.exit(1);            
        } catch (InterruptedException e) {
            logger.error("Error while sleeping...", e);
            System.exit(1);            
        } finally {
            try {
                if (producer != null)
                    producer.close();
                if (session != null)
                    session.close();
            } catch (HornetQException e) {
            }
        }
    }
}
