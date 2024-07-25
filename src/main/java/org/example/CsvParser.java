package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.ObjectData;
import org.example.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CsvParser implements Parser {
    private final int threadCount;

    public CsvParser(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public List<ObjectData> parse(Path filePath) throws IOException {
        long totalLines = Files.lines(filePath).count() - 1;
        long chunkSize = totalLines / threadCount;

        List<Future<List<ObjectData>>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try (CSVParser csvParser = CSVParser.parse(Files.newBufferedReader(filePath),
                CSVFormat.DEFAULT.withHeader())) {

            List<CSVRecord> records = csvParser.getRecords();
            for (int i = 0; i < threadCount; i++) {
                int start = (int) (i * chunkSize);
                int end = (int) ((i == threadCount - 1) ? totalLines : (i + 1) * chunkSize);
                futures.add(executor.submit(new CsvTask(records.subList(start, end))));
            }

            executor.shutdown();

            List<ObjectData> result = new ArrayList<>();
            for (Future<List<ObjectData>> future : futures) {
                result.addAll(future.get());
            }

            return result;
        } catch (Exception e) {
            throw new IOException("Error processing CSV file", e);
        }
    }

    private static class CsvTask implements Callable<List<ObjectData>> {
        private final List<CSVRecord> records;

        public CsvTask(List<CSVRecord> records) {
            this.records = records;
        }

        @Override
        public List<ObjectData> call() {
            List<ObjectData> objects = new ArrayList<>();
            for (CSVRecord record : records) {
                ObjectData obj = new ObjectData(
                        record.get("group"),
                        record.get("type"),
                        Long.parseLong(record.get("number")),
                        Long.parseLong(record.get("weight"))
                );
                objects.add(obj);
            }
            return objects;
        }
    }
}