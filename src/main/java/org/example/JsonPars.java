package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonPars {
    public static List<ObjectData> parse(Path filePath) throws IOException {
        long totalLines = Files.lines(filePath).count();
        long processedLines = 0;

        ObjectMapper mapper = new ObjectMapper();
        List<ObjectData> objects = new ArrayList<>();

        JsonNode rootNode = mapper.readTree(new File(filePath.toString()));


        if (!rootNode.isArray()) {
            throw new IOException("Expected JSON array");
        }


        for (JsonNode node : rootNode) {
            ObjectData obj = mapper.treeToValue(node, ObjectData.class);
            objects.add(obj);
            processedLines++;
            printProgress(processedLines, totalLines);
        }

        return objects;
    }

    private static void printProgress(long processedLines, long totalLines) {
        int progress = (int) ((processedLines * 100) / totalLines);
        System.out.print("\rProcessing JSON: " + progress + "%");
    }
}
