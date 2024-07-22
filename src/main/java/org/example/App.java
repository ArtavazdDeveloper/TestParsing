package org.example;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            try {
                System.out.println("Enter file path or 'exit' to quit:");
                input = scanner.nextLine();

                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                Path filePath = Paths.get(input);

                if (!Files.exists(filePath)) {
                    System.out.println("File does not exist.");
                    continue;
                }

                try {
                    List<ObjectData> objects;

                    if (input.endsWith(".csv")) {
                        objects = new CsvParser().parse(filePath);
                    } else if (input.endsWith(".json")) {
                        objects = new JsonPars().parse(filePath);
                    } else {
                        System.out.println("Unsupported file format.");
                        continue;
                    }

                    Statistics statistics = new Statistics(objects);
                    statistics.printStatistics();

                } catch (IOException e) {
                    System.out.println("Error reading file: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("An unexpected error occurred: " + e.getMessage());
                }

                System.out.println();
            } catch (NoSuchElementException e) {
                System.out.println("No input found. Exiting...");
                break;
            }
        }

        scanner.close();
    }
}