import java.util.*;

public class WordCountSimulator {

    // Simple container for intermediate key-value pairs
    static class KeyValuePair<K, V> {
        K key;
        V value;

        public KeyValuePair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    // 1. MAPPER: Splits incoming lines into tokens and emits (word, 1)
    public static List<KeyValuePair<String, Integer>> map(String line) {
        List<KeyValuePair<String, Integer>> mapOutput = new ArrayList<>();
        // Strip punctuation and split by whitespace
        String cleanLine = line.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();
        StringTokenizer tokenizer = new StringTokenizer(cleanLine);

        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            mapOutput.add(new KeyValuePair<>(word, 1));
        }
        return mapOutput;
    }

    // 2. SHUFFLE & SORT: Groups all intermediate values by key -> (word, [1, 1, 1...])
    public static Map<String, List<Integer>> shuffleAndSort(List<KeyValuePair<String, Integer>> intermediatePairs) {
        Map<String, List<Integer>> grouped = new TreeMap<>(); // TreeMap ensures sorted keys

        for (KeyValuePair<String, Integer> pair : intermediatePairs) {
            grouped.computeIfAbsent(pair.key, k -> new ArrayList<>()).add(pair.value);
        }
        return grouped;
    }

    // 3. REDUCER: Aggregates list of counts into a final sum
    public static int reduce(List<Integer> values) {
        int sum = 0;
        for (int count : values) {
            sum += count;
        }
        return sum;
    }

    public static void main(String[] args) {
        // Sample collection of documents / lines
        List<String> documents = Arrays.asList(
                "Distributed computing systems enable concurrent computation.",
                "MapReduce simplifies big data processing.",
                "Word count is the standard hello world of distributed MapReduce."
        );

        System.out.println("--- Input Documents ---");
        documents.forEach(doc -> System.out.println("> " + doc));

        // Step 1: Mapping
        List<KeyValuePair<String, Integer>> intermediatePairs = new ArrayList<>();
        for (String doc : documents) {
            intermediatePairs.addAll(map(doc));
        }

        System.out.println("\n--- Intermediate Mapped Pairs (Sample) ---");
        intermediatePairs.stream().limit(6).forEach(pair -> 
            System.out.println("(" + pair.key + ", " + pair.value + ")")
        );

        // Step 2: Shuffle & Sort
        Map<String, List<Integer>> shuffledData = shuffleAndSort(intermediatePairs);

        // Step 3: Reducing
        System.out.println("\n--- Final Word Counts (Reduce Output) ---");
        System.out.printf("%-20s | %s\n", "Word", "Count");
        System.out.println("---------------------|-------");

        for (Map.Entry<String, List<Integer>> entry : shuffledData.entrySet()) {
            int total = reduce(entry.getValue());
            System.out.printf("%-20s | %d\n", entry.getKey(), total);
        }
    }
}