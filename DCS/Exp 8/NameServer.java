import java.io.*;
import java.net.*;
import java.util.*;

public class NameServer {

    public static void main(String[] args) {

        // Name-to-address mapping
        Map<String, String> nameServer = new HashMap<>();

        nameServer.put("google", "142.250.195.14");
        nameServer.put("youtube", "142.250.72.14");
        nameServer.put("amazon", "205.251.242.103");
        nameServer.put("facebook", "157.240.241.35");

        try {

            // Create server socket
            ServerSocket serverSocket =
                    new ServerSocket(5000);

            System.out.println("Name Server started...");
            System.out.println("Waiting for client connection...");

            while (true) {

                // Accept client connection
                Socket clientSocket =
                        serverSocket.accept();

                System.out.println(
                        "\nClient connected: "
                                + clientSocket.getInetAddress()
                );

                // Receive name from client
                BufferedReader input =
                        new BufferedReader(
                                new InputStreamReader(
                                        clientSocket.getInputStream()
                                )
                        );

                String name = input.readLine().toLowerCase();

                System.out.println(
                        "Name requested: " + name
                );

                // Search for name
                String response;

                if (nameServer.containsKey(name)) {
                    response = nameServer.get(name);
                } else {
                    response = "Name not found";
                }

                // Send response
                PrintWriter output =
                        new PrintWriter(
                                clientSocket.getOutputStream(),
                                true
                        );

                output.println(response);

                // Close connection
                clientSocket.close();
            }

        } catch (IOException e) {

            System.out.println(
                    "Server Error: " + e.getMessage()
            );
        }
    }
}