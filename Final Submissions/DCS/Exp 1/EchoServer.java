import java.io.*;
import java.net.*;

class ClientHandler extends Thread {

    Socket socket;
    int clientNumber;

    ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
    }

    public void run() {

        try {
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter out =
                new PrintWriter(socket.getOutputStream(), true);

            String message;

            System.out.println("Client " + clientNumber + " connected.");

            while ((message = in.readLine()) != null) {

                System.out.println(
                    "Client " + clientNumber + ": " + message);

                if (message.equalsIgnoreCase("Exit"))
                    break;

                out.println(message);
            }

            socket.close();

            System.out.println(
                "Client " + clientNumber + " disconnected.");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

public class EchoServer {

    public static void main(String[] args) {

        try {

            ServerSocket serverSocket =
                new ServerSocket(5000);

            System.out.println("Echo Server started...");
            System.out.println("Waiting for clients...");

            int clientNumber = 0;

            while (true) {

                Socket socket = serverSocket.accept();

                clientNumber++;

                ClientHandler client =
                    new ClientHandler(socket, clientNumber);

                client.start();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}