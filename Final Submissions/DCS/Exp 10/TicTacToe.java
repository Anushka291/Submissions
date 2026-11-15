import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TicTacToe {

    static char[] b = {'1','2','3','4','5','6','7','8','9'};

    static void show() {
        System.out.printf("%n %c | %c | %c%n---+---+---%n %c | %c | %c%n---+---+---%n %c | %c | %c%n%n",
                b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8]);
    }

    static boolean win() {
        int[][] w = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
        for (int[] l : w)
            if (b[l[0]] == b[l[1]] && b[l[1]] == b[l[2]])
                return true;
        return false;
    }

    static boolean full() {
        for (char c : b)
            if (c != 'X' && c != 'O') return false;
        return true;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java TicTacToe <1|2>");
            return;
        }

        int player = Integer.parseInt(args[0]);
        char mine  = (player == 1) ? 'X' : 'O';
        char other = (player == 1) ? 'O' : 'X';

        Socket socket;
        if (player == 1) {
            ServerSocket server = new ServerSocket(5000);
            System.out.println("Player 1 (X) waiting for opponent...");
            socket = server.accept();
            server.close();
        } else {
            System.out.println("Player 2 (O) connecting...");
            socket = new Socket("localhost", 5000);
        }

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        Scanner sc = new Scanner(System.in);

        int turn = 1;   // Player 1 starts
        while (true) {
            show();
            int m;

            if (turn == player) {
                int pos;
                do {
                    System.out.print("Your move (1-9): ");
                    pos = sc.nextInt();
                } while (pos < 1 || pos > 9 || b[pos-1] == 'X' || b[pos-1] == 'O');
                m = pos - 1;
                b[m] = mine;
                out.writeInt(m);          // send move
                out.flush();
            } else {
                System.out.println("Waiting for opponent's move...");
                try {
                    m = in.readInt();     // receive move
                } catch (EOFException e) {
                    System.out.println("Opponent disconnected.");
                    break;
                }
                b[m] = other;
            }

            if (win()) {
                show();
                System.out.println(turn == player ? "You WIN!" : "You LOSE!");
                break;
            }
            if (full()) {
                show();
                System.out.println("It's a DRAW!");
                break;
            }
            turn = 3 - turn;              // switch turn
        }

        socket.close();
    }
}