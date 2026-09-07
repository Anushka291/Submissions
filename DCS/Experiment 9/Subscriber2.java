import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Subscriber2 {

    public static void main(String[] args) throws Exception {

        String brokerURL = "tcp://localhost:61616";

        ConnectionFactory factory =
                new ActiveMQConnectionFactory(brokerURL);

        JMSContext context = factory.createContext();

        Topic topic = context.createTopic("DCS_TOPIC");

        context.createConsumer(topic)
                .setMessageListener(message -> {
                    try {
                        System.out.println(
                                "Subscriber 2 received: "
                                + message.getBody(String.class)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        System.out.println(
                "Subscriber 2 is waiting for messages..."
        );

        // Keep subscriber alive
        Thread.sleep(60000);

        context.close();
    }
}