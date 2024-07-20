package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    public static List<ObjectData> parse(Path filePath) throws IOException {
        List<ObjectData> objects = new ArrayList<>();
        long totalLines = Files.lines(filePath).count() - 1; // Вычитаем 1, чтобы учесть заголовок
        long processedLines = 0;

        try (CSVParser csvParser = CSVParser.parse(Files.newBufferedReader(filePath),
                CSVFormat.DEFAULT.withHeader())) {
            for (CSVRecord record : csvParser) {
                ObjectData obj = new ObjectData(
                        record.get("group"),
                        record.get("type"),
                        Long.parseLong(record.get("number")),
                        Long.parseLong(record.get("weight"))
                );
                objects.add(obj);
                processedLines++;
                printProgress(processedLines, totalLines);
            }
        } catch (NumberFormatException e) {
            throw new IOException("Error parsing number or weight from CSV file", e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Error with CSV headers", e);
        }
        return objects;
    }
    private static void printProgress(long processedLines, long totalLines) {
        int progress = (int) ((processedLines * 100) / totalLines);
        System.out.print("\rProcessing CSV: " + progress + "%");
    }
}
