package org.example;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        // Scanner to read user input from the console
        try (Scanner scanner = new Scanner(System.in)) {
            int threadCount = 30; // Number of threads for processing

            while (true) {
                System.out.println("Enter file path or 'exit' to quit:");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting...");
                    break;
                }

                Path filePath = Paths.get(input);
                Parser parser;

                // Determine parser based on file extension
                if (input.toLowerCase().endsWith(".csv")) {
                    parser = new CsvParser(threadCount);
                } else if (input.toLowerCase().endsWith(".json")) {
                    parser = new JsonPars(threadCount);
                } else {
                    System.out.println("Unsupported file type. Please enter a .csv or .json file.");
                    continue;
                }

                try {
                    // Parse the file and process the data
                    List<ObjectData> result = parser.parse(filePath);
                    System.out.println("\nProcessing complete.");
                    // Perform additional analysis or output
                    Statistics statistics = new Statistics(result);
                    statistics.printStatistics();
                } catch (IOException e) {
                    System.out.println("Error processing file: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            // Handle unexpected exceptions from Scanner or other sources
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
