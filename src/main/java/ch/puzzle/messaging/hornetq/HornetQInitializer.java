package ch.puzzle.messaging.hornetq;

import ch.puzzle.messaging.Configuration;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hornetq.api.core.client.HornetQClient.createServerLocatorWithoutHA;

/**
 * Created by ben on 12.11.16.
 */
class HornetQInitializer {
    private static final String BROKER_DELIMITER = ",";
    private static final String PORT_DELIMITER = ":";

    private final Logger logger = LoggerFactory.getLogger(HornetQInitializer.class);

    ClientSession createSession(Configuration configuration) throws HornetQException {
        TransportConfiguration[] transportConfigurations = parseBrokerEndpoints(configuration);
        logger.info("Connecting to the following broker(s):");
        for (TransportConfiguration transportConfiguration : transportConfigurations) {
            logger.info(transportConfiguration.toString());
        }

        ServerLocator serverLocator = createServerLocatorWithoutHA(transportConfigurations);
        try {
            ClientSessionFactory factory = serverLocator.createSessionFactory();
            return factory.createSession(configuration.getUsername(), configuration
                    .getPassword(), configuration.isXa(), false, false, false, 1);

        } catch (Exception e) {
            logger.error("Could not create SessionFactory: {}", e.getMessage());
            return null;
        }
    }

    private TransportConfiguration[] parseBrokerEndpoints(Configuration configuration) {
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
