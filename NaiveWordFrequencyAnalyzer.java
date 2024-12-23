package firstprojectos;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class NaiveWordFrequencyAnalyzer {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Specify the path to the file
        String filePath = "C:\\Users\\ASUS\\Desktop\\os\\project\\text8.txt"; // Use relative path
        File file = new File(filePath);

        // Check if file exists
        if (!file.exists()) {
            System.err.println("File not found! Please ensure the file 'text8.txt' is in the correct directory.");
            System.exit(1); // Exit with error code
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Read the file into a single string
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(" ");
            }

            // Split text into words
            String[] words = content.toString().split("\\s+");

            // Count word frequencies
            Map<String, Integer> wordCounts = new HashMap<>();
            for (String word : words) {
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }

            // Sort by frequency and get the top 10
            List<Map.Entry<String, Integer>> sortedList = wordCounts.entrySet()
                    .stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(10)
                    .collect(Collectors.toList());

            // Output results
            System.out.println("Top 10 Words frequencies (Naive Approach):");
            for (Map.Entry<String, Integer> entry : sortedList) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution Time (Naive): " + (endTime - startTime) + " ms");
    }
}
