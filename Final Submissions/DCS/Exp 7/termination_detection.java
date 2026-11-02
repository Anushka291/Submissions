import java.util.Scanner;

class Process {

    int processId;
    String state;

    Process(int processId) {
        this.processId = processId;
        this.state = "ACTIVE";
    }

    void becomePassive() {
        state = "PASSIVE";
    }

    void becomeActive() {
        state = "ACTIVE";
    }
}


class TerminationDetector {

    Process[] processes;
    int messagesInTransit;

    TerminationDetector(Process[] processes) {
        this.processes = processes;
        this.messagesInTransit = 0;
    }

    void sendMessage(int sender, int receiver) {

        messagesInTransit++;

        System.out.println(
            "Process " + sender +
            " sent a message to Process " + receiver
        );
    }

    void receiveMessage(int receiver) {

        if (messagesInTransit > 0) {
            messagesInTransit--;
        }

        System.out.println(
            "Process " + receiver +
            " received the message"
        );
    }

    boolean checkTermination() {

        boolean allPassive = true;

        for (Process process : processes) {

            if (!process.state.equals("PASSIVE")) {
                allPassive = false;
                break;
            }
        }

        boolean noMessages = (messagesInTransit == 0);

        return allPassive && noMessages;
    }

    void displayStatus() {

        System.out.println("\n-----------------------------");
        System.out.println("Current System Status");
        System.out.println("-----------------------------");

        for (Process process : processes) {

            System.out.println(
                "Process " + process.processId +
                ": " + process.state
            );
        }

        System.out.println(
            "Messages in transit: " +
            messagesInTransit
        );
    }
}


public class termination_detection {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Input number of processes
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        // Create processes dynamically
        Process[] processes = new Process[n];

        for (int i = 0; i < n; i++) {
            processes[i] = new Process(i + 1);
        }

        // Create termination detector
        TerminationDetector detector =
            new TerminationDetector(processes);

        // Display initial state
        detector.displayStatus();

        // Enter sender and receiver
        System.out.print("\nEnter sender process: ");
        int sender = sc.nextInt();

        System.out.print("Enter receiver process: ");
        int receiver = sc.nextInt();

        // Send message
        detector.sendMessage(sender, receiver);

        detector.displayStatus();

        // Receive message
        System.out.print(
            "\nEnter process that received the message: "
        );

        int receivingProcess = sc.nextInt();

        detector.receiveMessage(receivingProcess);

        detector.displayStatus();

        // Make all processes passive
        System.out.println(
            "\nAll processes are becoming passive..."
        );

        for (Process process : processes) {
            process.becomePassive();
        }

        detector.displayStatus();

        // Check termination
        if (detector.checkTermination()) {

            System.out.println(
                "\nSYSTEM HAS TERMINATED"
            );

        } else {

            System.out.println(
                "\nSYSTEM HAS NOT TERMINATED"
            );
        }

        sc.close();
    }
}