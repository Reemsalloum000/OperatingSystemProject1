package firstprojectos;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MultithreadingWordFrequencyAnalyzer {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();

        // Read the file into a single string
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\ASUS\\Desktop\\os\\project\\\\text8.txt"));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append(" : ");
        }
        reader.close();

        String text = content.toString();
        int numThreads = 4; // Change this to 2, 4, 6, or 8
        int chunkSize = text.length() / numThreads;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        // Split text into chunks and assign to workers
        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? text.length() : (i + 1) * chunkSize;
            String chunk = text.substring(start, end);

            // Submit tasks to count words in each chunk
            futures.add(executor.submit(() -> {
                String[] words = chunk.split("\\s+");
                Map<String, Integer> wordCounts = new HashMap<>();
                for (String word : words) {
                    wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                }
                return wordCounts;
            }));
        }

        // Combine results
        Map<String, Integer> finalCounts = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> partialCounts = future.get();
            for (Map.Entry<String, Integer> entry : partialCounts.entrySet()) {
                finalCounts.put(entry.getKey(), finalCounts.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }

        executor.shutdown();

        // Sort by frequency
        List<Map.Entry<String, Integer>> sortedList = finalCounts.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .collect(Collectors.toList());

        // Output results
        System.out.println("Top 10 Words frequencies (Multithreading):");
        for (Map.Entry<String, Integer> entry : sortedList) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution Time (Multithreading): " + (endTime - startTime) + " ms");
    }
}

