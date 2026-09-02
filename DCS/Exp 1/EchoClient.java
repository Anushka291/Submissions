import java.io.*;
import java.net.*;

public class EchoClient {

    public static void main(String[] args) {

        try {

            Socket socket =
                new Socket("127.0.0.1", 5000);

            BufferedReader userInput =
                new BufferedReader(
                    new InputStreamReader(System.in));

            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter out =
                new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to Echo Server.");

            while (true) {

                System.out.print("Enter message: ");

                String message = userInput.readLine();

                out.println(message);

                if (message.equalsIgnoreCase("Exit"))
                    break;

                String response = in.readLine();

                System.out.println("Echo from Server: " + response);
            }

            socket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}