package ch.puzzle.messaging;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ben on 11.11.16.
 */
public class Configuration {
    public static final String SEND = "send";
    public static final String RECEIVE = "receive";
    public static final String HORNETQ_NATIVE = "hornetq";
    public static final String HORNETQ_JMS = "hornetq-jms";
    public static final String ARTEMIS_NATIVE = "artemis";
    public static final String ARTEMIS_JMS = "artemis-jms";

    private final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private PayloadGenerator payloadGenerator;

    @Option(name = "--protocol", usage = "defines the protocol to use. currently only hornetq is supported")
    private String protocol = "hornetq";

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
    private boolean ssl = false;

    @Option(name = "--loginterval", usage = "prints every nth message sent or received")
    private int loginterval = 1;

    private String payload;

    Configuration(String[] args) {
        payloadGenerator = new PayloadGenerator();
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
            payload = payloadGenerator.generatePayload(getSize());
        } catch (CmdLineException e) {
            logger.info(e.getMessage());
            logger.info("java -jar simple-client.jar [options...]");
            parser.printUsage(System.out);
            logger.info("  Example: java SampleMain" + parser.printExample(OptionHandlerFilter.ALL));
        }
    }

    public String getProtocol() {
        return protocol;
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

    public int getLoginterval() {
        return loginterval;
    }
}
