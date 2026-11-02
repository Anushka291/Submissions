import java.util.Scanner;

public class BerkeleyAlgorithm {

    // Converts "HH:mm:ss" or "HH:mm" format to total seconds
    private static int timeToSeconds(String timeStr) {
        String[] parts = timeStr.trim().split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = (parts.length == 3) ? Integer.parseInt(parts[2]) : 0;
        return (hours * 3600) + (minutes * 60) + seconds;
    }

    // Converts total seconds back to formatted "HH:mm:ss"
    private static String secondsToTime(int totalSeconds) {
        // Handle negative offsets or wrapping within 24 hours
        totalSeconds = (totalSeconds % 86400 + 86400) % 86400;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Berkeley Clock Synchronization Algorithm ===");
        
        System.out.print("Enter Master node's initial time (HH:mm:ss): ");
        String masterTimeStr = sc.nextLine();
        int masterTime = timeToSeconds(masterTimeStr);

        System.out.print("Enter the number of slave nodes: ");
        int numSlaves = sc.nextInt();
        sc.nextLine(); // consume newline

        int[] slaveTimes = new int[numSlaves];
        int[] timeDiffs = new int[numSlaves];

        for (int i = 0; i < numSlaves; i++) {
            System.out.print("Enter time for Slave " + (i + 1) + " (HH:mm:ss): ");
            String slaveTimeStr = sc.nextLine();
            slaveTimes[i] = timeToSeconds(slaveTimeStr);
            // Difference = Slave Time - Master Time
            timeDiffs[i] = slaveTimes[i] - masterTime;
        }

        // Master calculates average difference including its own difference (0)
        int sumOfDiffs = 0;
        for (int diff : timeDiffs) {
            sumOfDiffs += diff;
        }
        
        int averageDiff = sumOfDiffs / (numSlaves + 1);
        int synchronizedTime = masterTime + averageDiff;

        System.out.println("\n----------------- Synchronization Results -----------------");
        System.out.println("Average time difference adjustment: " + averageDiff + " seconds");
        System.out.println("Synchronized System Time: " + secondsToTime(synchronizedTime));
        System.out.println("-----------------------------------------------------------");

        // Master correction
        System.out.println("Master Node Adjustment: " + averageDiff + "s -> Synchronized: " + secondsToTime(synchronizedTime));

        // Individual slave corrections: adjustment = averageDiff - individual_difference
        for (int i = 0; i < numSlaves; i++) {
            int adjustment = averageDiff - timeDiffs[i];
            int updatedSlaveTime = slaveTimes[i] + adjustment;
            System.out.println("Slave " + (i + 1) + " Offset: " + adjustment + "s -> Synchronized: " + secondsToTime(updatedSlaveTime));
        }

        sc.close();
    }
}