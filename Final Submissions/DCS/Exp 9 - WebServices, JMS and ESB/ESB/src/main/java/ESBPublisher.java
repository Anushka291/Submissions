
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ESBPublisher {

    public static void main(String[] args) {

        // Connect to ActiveMQ
        ConnectionFactory factory =
                new ActiveMQConnectionFactory("tcp://localhost:61616");

        try (JMSContext context = factory.createContext()) {

            // Create ESB input queue
            Queue queue = context.createQueue("ESB_INPUT");

            // Send message to ESB
            String message = "Hello from ESB!";

            context.createProducer().send(queue, message);

            System.out.println("Message sent to ESB: " + message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
