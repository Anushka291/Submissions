import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RingAlgorithm {
    static int n;
    static int[] processIds;
    static boolean[] isAlive;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter total number of processes in the ring: ");
        n = sc.nextInt();

        processIds = new int[n];
        isAlive = new boolean[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Enter Process ID for ring position " + i + ": ");
            processIds[i] = sc.nextInt();
            isAlive[i] = true;
        }

        // Highest PID starts as coordinator
        int initialCoordinatorIndex = getMaxAliveIndex();
        System.out.println("\nInitial Coordinator is Process: " + processIds[initialCoordinatorIndex]);

        // Simulate crash
        System.out.println("Process " + processIds[initialCoordinatorIndex] + " has crashed.");
        isAlive[initialCoordinatorIndex] = false;

        System.out.print("\nEnter ring index of process initiating election (0 to " + (n - 1) + "): ");
        int initiator = sc.nextInt();

        runRingElection(initiator);
        sc.close();
    }

    private static void runRingElection(int initiator) {
        System.out.println("\n--- Starting Ring Election ---");
        List<Integer> activeList = new ArrayList<>();
        activeList.add(processIds[initiator]);

        int current = (initiator + 1) % n;

        // Pass election token around until it returns to initiator
        while (current != initiator) {
            if (isAlive[current]) {
                activeList.add(processIds[current]);
                System.out.println("Process " + processIds[current] + " received election message, added its ID to list: " + activeList);
            } else {
                System.out.println("Process " + processIds[current] + " is down. Skipping...");
            }
            current = (current + 1) % n;
        }

        System.out.println("Election message returned to initiator (Process " + processIds[initiator] + ")");
        System.out.println("Final Active List: " + activeList);

        // Determine maximum ID
        int newCoordinatorId = activeList.stream().max(Integer::compare).orElse(-1);
        System.out.println("\n>>> New Coordinator is Process ID: " + newCoordinatorId + " <<<");

        // Circulate coordinator notification message
        System.out.println("\nCirculating Coordinator Message around the ring...");
        current = (initiator + 1) % n;
        while (current != initiator) {
            if (isAlive[current]) {
                System.out.println("Process " + processIds[current] + " notified: Coordinator is " + newCoordinatorId);
            }
            current = (current + 1) % n;
        }
    }

    private static int getMaxAliveIndex() {
        int maxId = -1;
        int maxIndex = -1;
        for (int i = 0; i < n; i++) {
            if (isAlive[i] && processIds[i] > maxId) {
                maxId = processIds[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}