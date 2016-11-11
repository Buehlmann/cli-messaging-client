package ch.puzzle.messaging;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by ben on 11.11.16.
 */
public class Configuration {
    public static final String SEND = "send";
    public static final String RECEIVE = "receive";
    private final Logger logger = LoggerFactory.getLogger(Configuration.class);

    @Option(name = "--method", usage = "send or receive")
    private String method = "send";

    @Option(name = "--broker", usage = "string with the broker(s) and their messaging ports. e.g. broker1.localdomain:5500,broker2.localdomain:5500")
    private String brokers;

    @Option(name = "--destination", usage = "name of the queue or topic")
    private String destination;

    @Option(name = "--user", usage = "username used for authentication")
    private String username;

    @Option(name = "--password", usage = "password used for authentication")
    private String password;

    @Option(name = "--size", usage = "size in bytes of the message payload")
    private int size = 1024;

    @Option(name = "--count", usage = "repeat <n> times")
    private int count = 1;

    @Option(name = "--sleep", usage = "millisecond sleep period between count")
    private long sleep = 0;

    @Option(name = "--xa", usage = "enabling / disabling xa support")
    private boolean xa = false;

    @Option(name = "--ssl", usage = "enabling / disabling ssl encrypted message transfer")
    private Boolean ssl = false;

    private String payload;

    public Configuration(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
            payload = generatePayload(getSize());
        } catch (CmdLineException e) {
            logger.info(e.getMessage());
            logger.info("java -jar simple-client.jar [options...]");
            parser.printUsage(System.out);
            logger.info("  Example: java SampleMain" + parser.printExample(OptionHandlerFilter.ALL));
        }
    }

    private String generatePayload(int size) {
        Random r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append((char) (97 + r.nextInt(26)));
        }
        return sb.toString();
    }

    public String getMethod() {
        return method;
    }

    public String getDestination() {
        return destination;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getCount() {
        return count;
    }

    public String getBrokers() {
        return brokers;
    }

    public int getSize() {
        return size;
    }

    public long getSleep() {
        return sleep;
    }

    public boolean isXa() {
        return xa;
    }

    public String getPayload() {
        return payload;
    }

    public boolean isSsl() {
        return ssl;
    }
}
