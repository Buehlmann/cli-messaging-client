package ch.puzzle.messaging;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.*;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hornetq.api.core.client.HornetQClient.createServerLocatorWithoutHA;

/**
 * Created by ben on 11.11.16.
 */
public class HornetQSender {
    public static final String BROKER_DELIMITER = ",";
    public static final String PORT_DELIMITER = ":";

    private final Logger logger = LoggerFactory.getLogger(HornetQSender.class);

    private Configuration configuration;

    public HornetQSender(Configuration configuration) {
        this.configuration = configuration;
    }

    public void process() {
        TransportConfiguration[] transportConfigurations = parseBrokerEndpoints();
        logger.info("Connecting to the following broker(s):");
        for (TransportConfiguration transportConfiguration : transportConfigurations) {
            logger.info(transportConfiguration.toString());
        }
        ServerLocator serverLocator = createServerLocatorWithoutHA(transportConfigurations);
        ClientSessionFactory factory = null;
        try {
            factory = serverLocator.createSessionFactory();
        } catch (Exception e) {
            logger.error("Could not create SessionFactory: {}", e.getMessage());
        }

        ClientSession session = null;
        ClientProducer producer = null;
        try {
            session = factory.createSession(configuration.getUsername(), configuration
                    .getPassword(), configuration.isXa(), false, false, false, 1);

            producer = session.createProducer(configuration.getDestination());

            for (int i = 0; i < configuration.getCount(); i++) {
                try {
                    ClientMessage message = session.createMessage(true);
                    message.getBodyBuffer().writeString(configuration.getPayload());
                    producer.send(message);
                    session.commit();
                    logger.info("Sent message #{}", i + 1);
                } catch (HornetQException e) {
                    logger.error("Error occured while trying to send message: {}", e.getMessage());
                }

                if (configuration.getSleep() > 0) {
                    Thread.currentThread().sleep(configuration.getSleep());
                }
            }

        } catch (HornetQException e) {
            logger.error("Error occured while trying to connect to broker: {}", e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Error while sleeping...", e);
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

    private TransportConfiguration[] parseBrokerEndpoints() {
        List<TransportConfiguration> brokers = new ArrayList<>();

        for (String s : configuration.getBrokers().split(BROKER_DELIMITER)) {
            String[] broker = s.split(PORT_DELIMITER);
            Map<String, Object> map = new HashMap<>();
            map.put("host", broker[0]);
            map.put("port", broker[1]);
            map.put("ssl-enabled", configuration.isSsl());
            brokers.add(new TransportConfiguration(NettyConnectorFactory.class.getName(), map));
        }
        return brokers.toArray(new TransportConfiguration[0]);
    }
}
