# CLI based Messaging Client
Provides a simple command line based client to send and receive Messages from a java based message broker.

## Supported Messaging Protocols
* HornetQ core protocol

## Build the client
```
mvn clean package
```

## Run the client
```
java -jar messaging-client.jar
```

## Supported parameters of the messaging client
```
> java -jar simple-client.jar [options...]
 --broker VAL      : string with the broker(s) and their messaging ports. e.g.
                     brokercx1.localdomain:5500,broker2.localdomain:5500
 --count N         : repeat <n> times (default: 1)
 --destination VAL : name of the queue or topic
 --loginterval N   : prints every nth message sent or received (default: 1)
 --method VAL      : send or receive (default: send)
 --password VAL    : password used for authentication
 --protocol VAL    : defines the protocol to use. currently only hornetq is
                     supported (default: hornetq)
 --size N          : size in bytes of the message payload (default: 1024)
 --sleep N         : millisecond sleep period between count (default: 0)
 --ssl             : enabling / disabling ssl encrypted message transfer
                     (default: false)
 --user VAL        : username used for authentication
 --xa              : enabling / disabling xa support (default: false)
```

## Planned features
* Failover Handling
* Support of Artemis core protocol
* Integration of the JMS API (@see http://docs.jboss.org/hornetq/2.3.0.Final/docs/user-manual/html/using-jms.html#d0e1361)