package org.example;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class JsonPars {

    public List<ObjectData> parse(Path filePath, int threadCount) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory jsonFactory = mapper.getFactory();

        try (JsonParser parser = jsonFactory.createParser(new File(filePath.toString()))) {
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IOException("Expected JSON array");
            }

            List<ObjectData> objects = new ArrayList<>();
            int totalLines = 0;

            while (parser.nextToken() != JsonToken.END_ARRAY) {
                totalLines++;
            }

            parser.close();

            long chunkSize = totalLines / threadCount;

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<List<ObjectData>>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                long start = i * chunkSize;
                long end = (i == threadCount - 1) ? totalLines : (i + 1) * chunkSize;
                futures.add(executor.submit(new JsonTask(new File(filePath.toString()), mapper, start, end)));
            }

            executor.shutdown();

            for (Future<List<ObjectData>> future : futures) {
                objects.addAll(future.get());
            }

            return objects;
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error processing JSON file", e);
        }
    }

    private static class JsonTask implements Callable<List<ObjectData>> {
        private final File file;
        private final ObjectMapper mapper;
        private long start;
        private final long end;

        public JsonTask(File file, ObjectMapper mapper, long start, long end) {
            this.file = file;
            this.mapper = mapper;
            this.start = start;
            this.end = end;
        }

        @Override
        public List<ObjectData> call() throws IOException {
            List<ObjectData> objects = new ArrayList<>();
            JsonFactory jsonFactory = mapper.getFactory();

            try (JsonParser parser = jsonFactory.createParser(file)) {
                if (parser.nextToken() != JsonToken.START_ARRAY) {
                    throw new IOException("Expected JSON array");
                }

                long currentIndex = 0;

                while (currentIndex < start && parser.nextToken() != JsonToken.END_ARRAY) {
                    currentIndex++;
                }

                while (currentIndex < end && parser.nextToken() != JsonToken.END_ARRAY) {
                    ObjectData obj = mapper.readValue(parser, ObjectData.class);
                    objects.add(obj);
                    currentIndex++;
                }
            }

            return objects;
        }
    }
}
