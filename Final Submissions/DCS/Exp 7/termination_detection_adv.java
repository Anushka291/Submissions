
import java.util.*;

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

class Message {

    int sender;
    int receiver;

    Message(int sender, int receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
}

class TerminationDetector {

    Process[] processes;
    ArrayList<Message> messages;

    TerminationDetector(Process[] processes) {
        this.processes = processes;
        messages = new ArrayList<>();
    }

    void sendMessage(int sender, int receiver) {

        if (sender < 1 || sender > processes.length ||
            receiver < 1 || receiver > processes.length) {

            System.out.println("Invalid process ID!");
            return;
        }

        if (processes[sender - 1].state.equals("PASSIVE")) {
            System.out.println("Passive process cannot send a message!");
            return;
        }

        messages.add(new Message(sender, receiver));

        System.out.println(
            "Process " + sender +
            " sent a message to Process " + receiver
        );
    }

    void receiveMessage(int receiver) {

        if (receiver < 1 || receiver > processes.length) {
            System.out.println("Invalid process ID!");
            return;
        }

        for (int i = 0; i < messages.size(); i++) {

            Message msg = messages.get(i);

            if (msg.receiver == receiver) {

                messages.remove(i);

                processes[receiver - 1].becomeActive();

                System.out.println(
                    "Process " + receiver +
                    " received message from Process " +
                    msg.sender
                );

                return;
            }
        }

        System.out.println("No message available for Process " + receiver);
    }

    void checkTermination() {

        boolean allPassive = true;

        for (Process process : processes) {

            if (process.state.equals("ACTIVE")) {
                allPassive = false;
                break;
            }
        }

        boolean noMessages = messages.isEmpty();

        if (allPassive && noMessages) {
            System.out.println("\nSYSTEM HAS TERMINATED");
        } else {
            System.out.println("\nSYSTEM HAS NOT TERMINATED");
        }
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
            "Messages in transit: " + messages.size()
        );
    }
}

public class termination_detection_adv {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        if (n <= 0) {
            System.out.println("Number of processes must be positive!");
            sc.close();
            return;
        }

        Process[] processes = new Process[n];

        for (int i = 0; i < n; i++) {
            processes[i] = new Process(i + 1);
        }

        TerminationDetector detector =
            new TerminationDetector(processes);

        int choice;

        do {

            System.out.println("\n===== TERMINATION DETECTION =====");
            System.out.println("1. Send Message");
            System.out.println("2. Receive Message");
            System.out.println("3. Make Process Passive");
            System.out.println("4. Make Process Active");
            System.out.println("5. Display System Status");
            System.out.println("6. Check Termination");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:

                    System.out.print("Enter sender process: ");
                    int sender = sc.nextInt();

                    System.out.print("Enter receiver process: ");
                    int receiver = sc.nextInt();

                    detector.sendMessage(sender, receiver);
                    break;

                case 2:

                    System.out.print("Enter receiving process: ");
                    int receivingProcess = sc.nextInt();

                    detector.receiveMessage(receivingProcess);
                    break;

                case 3:

                    System.out.print("Enter process ID: ");
                    int passiveId = sc.nextInt();

                    if (passiveId >= 1 && passiveId <= n) {
                        processes[passiveId - 1].becomePassive();
                        System.out.println("Process became PASSIVE.");
                    } else {
                        System.out.println("Invalid process ID!");
                    }
                    break;

                case 4:

                    System.out.print("Enter process ID: ");
                    int activeId = sc.nextInt();

                    if (activeId >= 1 && activeId <= n) {
                        processes[activeId - 1].becomeActive();
                        System.out.println("Process became ACTIVE.");
                    } else {
                        System.out.println("Invalid process ID!");
                    }
                    break;

                case 5:

                    detector.displayStatus();
                    break;

                case 6:

                    detector.displayStatus();
                    detector.checkTermination();
                    break;

                case 7:

                    System.out.println("Exiting program...");
                    break;

                default:

                    System.out.println("Invalid choice!");
            }

        } while (choice != 7);

        sc.close();
    }
}
