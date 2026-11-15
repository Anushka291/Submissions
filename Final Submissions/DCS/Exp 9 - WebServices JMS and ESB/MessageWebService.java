
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class MessageWebService {

    public static void main(String[] args) throws Exception {

        String brokerURL = "tcp://localhost:61616";

        ConnectionFactory factory =
                new ActiveMQConnectionFactory(brokerURL);

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8087), 0);

        server.createContext("/publish", exchange -> {

            if (!exchange.getRequestMethod().equals("POST")) {
                sendResponse(exchange, 405,
                        "Only POST requests are allowed");
                return;
            }

            try {
                String message = new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8);

                try (JMSContext context =
                             factory.createContext()) {

                    Topic topic =
                            context.createTopic("DCS_TOPIC");

                    context.createProducer().send(topic, message);
                }

                System.out.println(
                        "Web Service published: " + message);

                sendResponse(exchange, 200,
                        "Message published successfully!");

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500,
                        "Message publishing failed");
            }
        });

        server.start();

        System.out.println(
                "Web Service started at http://localhost:8087");

        System.out.println(
                "Waiting for HTTP requests...");
    }

    private static void sendResponse(
            HttpExchange exchange,
            int status,
            String response) throws IOException {

        byte[] bytes = response.getBytes(
                StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "text/plain; charset=UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);

        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
