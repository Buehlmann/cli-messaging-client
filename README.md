Simple Messaging Client
=======================
Provides a simple command line based client to send and receive Messages from a java based broker.

# Supported Messaging Protocols
* HornetQ core protocol

# Build the client-jar
```
mvn clean package
```

# Run the client jar
```
java -jar messaging-client.jar
```

# Planned features
* Failover Handling
* Support of Artemis core protocol
* Integration of the JMS API (@see http://docs.jboss.org/hornetq/2.3.0.Final/docs/user-manual/html/using-jms.html#d0e1361)