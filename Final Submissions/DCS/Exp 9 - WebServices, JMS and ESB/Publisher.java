import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Publisher {

    public static void main(String[] args) {

        String brokerURL = "tcp://localhost:61616";

        ConnectionFactory factory =
                new ActiveMQConnectionFactory(brokerURL);

        try (JMSContext context = factory.createContext()) {

            Topic topic = context.createTopic("DCS_TOPIC");

            System.out.println("Publishing message...");

            context.createProducer().send(
                    topic,
                    "Hello from Publisher!"
            );

            System.out.println("Message published successfully.");
        }
    }
}