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
public class NativeHornetQReceiver {
    private final Logger logger = LoggerFactory.getLogger(NativeHornetQReceiver.class);

    private Configuration configuration;
    private HornetQInitializer initializer;

    public NativeHornetQReceiver(Configuration configuration) {
        this.configuration = configuration;
        this.initializer = new HornetQInitializer(configuration);
    }

    public void process() {
        ClientSession session = null;
        ClientConsumer consumer = null;

        try {
            session = initializer.createNativeSession();
            if (session == null) {
                logger.error("Could not create client session");
                System.exit(1);
            }
            session.start();
            consumer = session.createConsumer(configuration.getDestination());
            logger.debug("Successfully connected to message broker {}", session);
            logger.debug("Waiting for messages...");

            for (int i = 0; i < configuration.getCount(); i++) {
                try {
                    ClientMessage message;
                    if(configuration.getReceiveTimeout() == null) {
                        message = consumer.receive();
                    } else {
                        message = consumer.receive(configuration.getReceiveTimeout());
                    }
                    if(message == null) {
                        logger.error("Did not receive any expected message during configured timeout of {} ms", configuration.getReceiveTimeout());                        
                        System.exit(1);
                    }
                    if (consumer.isClosed()) {
                        logger.error("Consumer is closed - exiting. Failover?");
                        System.exit(1);
                    }
                    message.acknowledge();
                    if (i % configuration.getLoginterval() == 0) {
                        if (!configuration.isPrintMessageBody()) {
                            logger.info("Received message #{}: {}, body size in bytes: {}", i + 1, message, message.getBodySize());                            
                        } else {                            
                            logger.info("Received message #{}: {}, body size in bytes: {}, message body: \"{}\"", i + 1, message, message.getBodySize(), message.getBodyBuffer().readString());
                        }
                    }
                } catch (HornetQException e) {
                    logger.error("Error occured while receiving a message: {}", e.getMessage());
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
