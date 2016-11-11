package ch.puzzle.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ben on 11.11.16.
 */
public class HornetQSender {
    private final Logger logger = LoggerFactory.getLogger(HornetQSender.class);

    private Configuration configuration;

    public HornetQSender(Configuration configuration) {
        this.configuration = configuration;
    }

    public void process() {
        logger.info("Sending Messages...");

        // TODO: implement sending of messages

        logger.info("Successfully sent {} messages", configuration.getCount());
    }
}
