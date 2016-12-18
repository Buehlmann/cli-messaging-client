package ch.puzzle.messaging.jms;

import ch.puzzle.messaging.Configuration;
import ch.puzzle.messaging.artemis.ArtemisInitializer;
import ch.puzzle.messaging.hornetq.HornetQInitializer;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

import static ch.puzzle.messaging.Configuration.ARTEMIS_JMS;
import static ch.puzzle.messaging.Configuration.HORNETQ_JMS;

/**
 * Created by ben on 18.11.16.
 */
public class JMSInitializer {
    private final Logger logger = LoggerFactory.getLogger(JMSInitializer.class);

    private Configuration configuration;

    public JMSInitializer(Configuration configuration) {
        this.configuration = configuration;
    }


    public Session createJMSSession() throws JMSException {
        try {
            Connection connection = createJMSConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            return session;
        } catch (JMSException e) {
            logger.error("Could not open Connection or Session: {}", e.getMessage());
            throw e;
        }
    }

    private Connection createJMSConnection() throws JMSException {
        switch (configuration.getProtocol()) {
            case HORNETQ_JMS:
                TransportConfiguration[] hqTransports = new HornetQInitializer(configuration).parseBrokerEndpoints();
                HornetQConnectionFactory hqCF = org.hornetq.api.jms.HornetQJMSClient.createConnectionFactoryWithHA(configuration.isXa() ? org.hornetq.api.jms.JMSFactoryType.XA_CF : org.hornetq.api.jms.JMSFactoryType.CF, hqTransports);
                hqCF.setReconnectAttempts(-1);
                return configuration.isXa() ?
                        hqCF.createXAConnection(configuration.getUsername(), configuration.getPassword()) :
                        hqCF.createConnection(configuration.getUsername(), configuration.getPassword());

            case ARTEMIS_JMS:
                org.apache.activemq.artemis.api.core.TransportConfiguration[] artemisTransports = new ArtemisInitializer(configuration).parseBrokerEndpoints();
                ActiveMQConnectionFactory artemisCF = ActiveMQJMSClient.createConnectionFactoryWithHA(configuration.isXa() ? org.apache.activemq.artemis.api.jms.JMSFactoryType.XA_CF : org.apache.activemq.artemis.api.jms.JMSFactoryType.CF, artemisTransports);
                artemisCF.setReconnectAttempts(-1);
                return configuration.isXa() ?
                        artemisCF.createXAConnection(configuration.getUsername(), configuration.getPassword()) :
                        artemisCF.createConnection(configuration.getUsername(), configuration.getPassword());

            default:
                logger.error("Not supported protocol: {}", configuration.getProtocol());
                return null;
        }
    }

    public Queue createJMSQueue() {
        switch (configuration.getProtocol()) {
            case HORNETQ_JMS:
                return HornetQJMSClient.createQueue(configuration.getDestination());

            case ARTEMIS_JMS:
                return ActiveMQJMSClient.createQueue(configuration.getDestination());

            default:
                logger.error("Not supported protocol: {}", configuration.getProtocol());
                return null;
        }
    }
}