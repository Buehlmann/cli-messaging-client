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
    private final Logger logger = LoggerFactory.getLogger(Configuration.class);

    public static final String SEND = "send";
    public static final String RECEIVE = "receive";

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
    private int sleep = 0;


    public Configuration(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            logger.info(e.getMessage());
            logger.info("java -jar simple-client.jar [options...]");
            parser.printUsage(System.out);

            // print option sample. This is useful some time
            logger.info("  Example: java SampleMain" + parser.printExample(OptionHandlerFilter.ALL));

            return;
        }
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

    public int getSleep() {
        return sleep;
    }
}
