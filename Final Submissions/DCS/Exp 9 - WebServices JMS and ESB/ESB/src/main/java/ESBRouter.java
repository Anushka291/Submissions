
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.component.jms.JmsComponent;

public class ESBRouter {

    public static void main(String[] args) throws Exception {

        // Connect to ActiveMQ broker
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory("tcp://localhost:61616");

        // Create Camel context
        CamelContext context = new DefaultCamelContext();

        // Register JMS component
        context.addComponent(
                "jms",
                JmsComponent.jmsComponentAutoAcknowledge(factory)
        );

        // Define ESB routing logic
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {

                from("jms:queue:ESB_INPUT")
                    .routeId("ESB-Publish-Subscribe")
                    .log("ESB received message: ${body}")
                    .to("jms:topic:DCS_TOPIC")
                    .log("ESB forwarded message to DCS_TOPIC");
            }
        });

        // Start ESB
        context.start();

        System.out.println("ESB Router started successfully!");
        System.out.println("Waiting for messages from ESB_INPUT...");

        // Keep the application running
        Thread.sleep(Long.MAX_VALUE);

        context.stop();
    }
}
