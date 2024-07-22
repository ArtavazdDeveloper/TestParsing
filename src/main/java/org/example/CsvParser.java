package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    public List<ObjectData> parse(Path filePath) throws IOException {
        List<ObjectData> objects = new ArrayList<>();
        long totalLines = Files.lines(filePath).count() - 1; // Subtract 1 to account for header
        long processedLines = 0;

        try (CSVParser csvParser = CSVParser.parse(Files.newBufferedReader(filePath),
                CSVFormat.DEFAULT.withHeader())) {
            for (CSVRecord record : csvParser) {
                try {
                    ObjectData obj = new ObjectData(
                            record.get("group"),
                            record.get("type"),
                            Long.parseLong(record.get("number")),
                            Long.parseLong(record.get("weight"))
                    );
                    objects.add(obj);
                } catch (NumberFormatException e) {
                    throw new IOException("Error parsing number or weight from CSV file", e);
                }
                processedLines++;
                printProgress(processedLines, totalLines);
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Error with CSV headers", e);
        }
        return objects;
    }

    private void printProgress(long processedLines, long totalLines) {
        int progress = (int) ((processedLines * 100) / totalLines);
        System.out.print("\rProcessing CSV: " + progress + "%");
    }
}

