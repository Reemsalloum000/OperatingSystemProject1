package firstprojectos;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiprocessingWordFrequencyAnalyzer {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Specify the path to the file
        String filePath = "C:\\Users\\ASUS\\Desktop\\os\\project\\text8.txt"; // File path
        File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            System.err.println("File not found! Please ensure the file is in the correct location.");
            System.exit(1); // Exit if the file does not exist
        }

        System.out.println("File found. Starting processing...");

        int numProcesses = 2; // Number of parallel processes (reduced to save memory)

        try {
            // Split the file into chunks for parallel processing
            List<String> chunks = splitFileIntoChunks(file, numProcesses);

            // Use ExecutorService to handle parallel processing
            ExecutorService executor = Executors.newFixedThreadPool(numProcesses);
            List<Future<Map<String, Integer>>> futures = new ArrayList<>();

            // Submit each chunk for processing in a separate thread
            for (int i = 0; i < chunks.size(); i++) {
                String chunk = chunks.get(i);
                System.out.println("Submitting chunk " + (i + 1) + " for processing...");
                futures.add(executor.submit(() -> countWords(chunk)));
            }

            executor.shutdown();

            // Wait for all processes to finish
            Map<String, Integer> combinedFrequency = new ConcurrentHashMap<>();
            for (int i = 0; i < futures.size(); i++) {
                System.out.println("Waiting for process " + (i + 1) + " to finish...");
                Map<String, Integer> partialFrequency = futures.get(i).get();
                mergeFrequencies(combinedFrequency, partialFrequency);
                System.out.println("Process " + (i + 1) + " finished.");
            }

            // Get the top 10 most frequent words
            List<Map.Entry<String, Integer>> sortedEntries = getTopWords(combinedFrequency, 10);

            // Print the results in the desired format
            System.out.println("Top 10 Most Frequent Words:");
            for (int i = 0; i < sortedEntries.size(); i++) {
                System.out.println((i + 1) + ". " + sortedEntries.get(i).getKey() + ": " + sortedEntries.get(i).getValue());
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.err.println("An error occurred: " + e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution Time (Multiprocessing): " + (endTime - startTime) + " ms");
    }

    // Split the file into chunks for parallel processing
    private static List<String> splitFileIntoChunks(File file, int numChunks) throws IOException {
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        long fileSize = file.length();
        long chunkSize = fileSize / numChunks;  // Calculate size of each chunk

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            long currentSize = 0;
            while ((line = reader.readLine()) != null) {
                currentChunk.append(line).append(" ");
                currentSize += line.length();
                if (currentSize >= chunkSize) {
                    chunks.add(currentChunk.toString()); // Add the chunk to the list
                    currentChunk.setLength(0); // Reset the chunk
                    currentSize = 0;
                }
            }
        }

        // Add the last chunk if there is any remaining data
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }
        return chunks;
    }

    // Count the frequency of words in a chunk of text
    private static Map<String, Integer> countWords(String text) {
        String[] words = text.split("\\s+");
        Map<String, Integer> wordFrequency = new HashMap<>();
        for (String word : words) {
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1); // Count occurrences of each word
        }
        return wordFrequency;
    }

    // Merge the word frequencies from multiple chunks into the final result
    private static void mergeFrequencies(Map<String, Integer> combinedFrequency, Map<String, Integer> partialFrequency) {
        for (Map.Entry<String, Integer> entry : partialFrequency.entrySet()) {
            combinedFrequency.put(entry.getKey(), combinedFrequency.getOrDefault(entry.getKey(), 0) + entry.getValue()); // Merge frequencies
        }
    }

    // Get the top N most frequent words
    private static List<Map.Entry<String, Integer>> getTopWords(Map<String, Integer> wordFrequency, int n) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordFrequency.entrySet()); // Convert map to list for sorting
        list.sort((a, b) -> b.getValue() - a.getValue());  // Sort by frequency in descending order
        return list.subList(0, Math.min(n, list.size()));  // Return the top N words
    }
}

