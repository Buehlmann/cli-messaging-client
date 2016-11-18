package ch.puzzle.messaging.hornetq;

import ch.puzzle.messaging.Configuration;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hornetq.api.core.client.HornetQClient.createServerLocatorWithoutHA;
import static org.hornetq.api.jms.HornetQJMSClient.createConnectionFactoryWithoutHA;
import static org.hornetq.api.jms.JMSFactoryType.CF;
import static org.hornetq.api.jms.JMSFactoryType.XA_CF;

/**
 * Created by ben on 12.11.16.
 */
class HornetQInitializer {
    private static final String BROKER_DELIMITER = ",";
    private static final String PORT_DELIMITER = ":";

    private final Logger logger = LoggerFactory.getLogger(HornetQInitializer.class);

    ClientSession createNativeSession(Configuration configuration) {
        TransportConfiguration[] transportConfigurations = parseBrokerEndpoints(configuration);
        logger.info("Connecting to the following broker(s):");
        for (TransportConfiguration transportConfiguration : transportConfigurations) {
            logger.info(transportConfiguration.toString());
        }

        ServerLocator serverLocator = createServerLocatorWithoutHA(transportConfigurations);
        try {
            ClientSessionFactory factory = serverLocator.createSessionFactory();
            return factory.createSession(configuration.getUsername(), configuration.getPassword(),
                    configuration.isXa(), true, true, serverLocator.isPreAcknowledge(),
                    serverLocator.getAckBatchSize());

        } catch (Exception e) {
            logger.error("Could not create SessionFactory: {}", e.getMessage());
            return null;
        }
    }

    Session createJMSSession(Configuration configuration) throws JMSException {
        TransportConfiguration[] transportConfigurations = parseBrokerEndpoints(configuration);
        HornetQConnectionFactory cf = createConnectionFactoryWithoutHA(configuration.isXa() ? XA_CF : CF, transportConfigurations);

        try {
            Connection connection = cf.createConnection(configuration.getUsername(), configuration.getPassword());
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            return session;
        } catch (JMSException e) {
            logger.error("Could not open Connection or Session: {}", e.getMessage());
            throw e;
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
            TransportConfiguration transport = new TransportConfiguration(NettyConnectorFactory.class.getName(), map);
            brokers.add(transport);
            logger.info(transport.toString());
        }
        return brokers.toArray(new TransportConfiguration[0]);
    }
}
