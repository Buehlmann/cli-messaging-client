package ch.puzzle.messaging.hornetq;

import ch.puzzle.messaging.Configuration;
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
public class HornetQInitializer {
    private static final String BROKER_DELIMITER = ",";
    private static final String PORT_DELIMITER = ":";

    private final Logger logger = LoggerFactory.getLogger(HornetQInitializer.class);

    private Configuration configuration;

    public HornetQInitializer(Configuration configuration) {
        this.configuration = configuration;
    }

    ClientSession createNativeSession() {
        TransportConfiguration[] transportConfigurations = parseBrokerEndpoints();
        logger.debug("Connecting to the following broker(s):");
        for (TransportConfiguration transportConfiguration : transportConfigurations) {
            logger.debug(transportConfiguration.toString());
        }

        ServerLocator serverLocator = createServerLocatorWithoutHA(transportConfigurations);
        try {
            ClientSessionFactory factory = serverLocator.createSessionFactory();
            return factory.createSession(configuration.getUsername(), configuration.getPassword(),
                    configuration.isXa(), true, true, serverLocator.isPreAcknowledge(),
                    serverLocator.getAckBatchSize());

        } catch (Exception e) {
            logger.error("Could not create SessionFactory: {}", e.getMessage());
            System.exit(1);
            return null;
        }
    }

    public TransportConfiguration[] parseBrokerEndpoints() {
        List<TransportConfiguration> brokers = new ArrayList<>();

        for (String s : configuration.getBrokers().split(BROKER_DELIMITER)) {
            String[] broker = s.split(PORT_DELIMITER);
            Map<String, Object> map = new HashMap<>();
            map.put("host", broker[0]);
            map.put("port", broker[1]);
            map.put("ssl-enabled", configuration.isSsl());
            TransportConfiguration transport = new TransportConfiguration(NettyConnectorFactory.class.getName(), map);
            brokers.add(transport);
            logger.debug(transport.toString());
        }
        return brokers.toArray(new TransportConfiguration[0]);
    }
}
