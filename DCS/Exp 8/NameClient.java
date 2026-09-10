import java.io.*;
import java.net.*;
import java.util.Scanner;

public class NameClient {

    public static void main(String[] args) {

        try {

            // Create socket and connect to Name Server
            Socket clientSocket =
                    new Socket("127.0.0.1", 5000);

            // Take name from user
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter name: ");
            String name = sc.nextLine();

            // Send name to server
            PrintWriter output =
                    new PrintWriter(
                            clientSocket.getOutputStream(),
                            true
                    );

            output.println(name);

            // Receive response
            BufferedReader input =
                    new BufferedReader(
                            new InputStreamReader(
                                    clientSocket.getInputStream()
                            )
                    );

            String response = input.readLine();

            System.out.println(
                    "Server response: " + response
            );

            // Close connection
            clientSocket.close();
            sc.close();

        } catch (IOException e) {

            System.out.println(
                    "Client Error: " + e.getMessage()
            );
        }
    }
}