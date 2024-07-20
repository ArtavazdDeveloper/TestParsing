package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;
        while (true) {
            System.out.println("Enter file path or 'exit' to quit:");
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            Path filePath = Paths.get(input);
            try {
                if (input.endsWith(".csv")) {
                    List<ObjectData> objects = CsvParser.parse(filePath);
                    Statistics statistics = new Statistics(objects);
                    statistics.printStatistics();
                } else if (input.endsWith(".json")) {
                    List<ObjectData> objects = JsonPars.parse(filePath);
                    Statistics statistics = new Statistics(objects);
                    statistics.printStatistics();
                } else {
                    System.out.println("Unsupported file format.");
                }
                System.out.println();
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
        scanner.close();
    }
}