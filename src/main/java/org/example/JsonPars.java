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

public class JsonPars implements Parser {
    @Override
    public List<ObjectData> parse(Path filePath) throws IOException {
        List<ObjectData> objects = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory jsonFactory = mapper.getFactory();

        try (JsonParser parser = jsonFactory.createParser(new File(filePath.toString()))) {
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IOException("Expected JSON array");
            }

            long totalLines = 0;
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                totalLines++;
            }

            parser.close();

            try (JsonParser streamingParser = jsonFactory.createParser(new File(filePath.toString()))) {
                if (streamingParser.nextToken() != JsonToken.START_ARRAY) {
                    throw new IOException("Expected JSON array");
                }

                long processedLines = 0;
                while (streamingParser.nextToken() != JsonToken.END_ARRAY) {
                    ObjectData obj = mapper.readValue(streamingParser, ObjectData.class);
                    objects.add(obj);
                    processedLines++;
                    printProgress(processedLines, totalLines);
                }
            }
        }

        return objects;
    }

    private void printProgress(long processedLines, long totalLines) {
        int progress = (int) ((processedLines * 100) / totalLines);
        System.out.print("\rProcessing JSON: " + progress + "%");
    }
}
