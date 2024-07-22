package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParsTest {

    private Path tempFile;
    private JsonPars jsonPars; // Add an instance variable for JsonPars

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".json");
        jsonPars = new JsonPars(); // Initialize the instance
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testParse() throws IOException {
        String jsonData = "[{\"group\":\"group1\",\"type\":\"type1\",\"number\":1,\"weight\":10}," +
                "{\"group\":\"group2\",\"type\":\"type2\",\"number\":2,\"weight\":20}]";
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write(jsonData);
        }
        List<ObjectData> actualObjects = jsonPars.parse(tempFile); // Use the instance to call parse
        assertNotNull(actualObjects);
        assertEquals(2, actualObjects.size());

        ObjectData first = actualObjects.get(0);
        assertEquals("group1", first.getGroup());
        assertEquals("type1", first.getType());
        assertEquals(1, first.getNumber());
        assertEquals(10, first.getWeight());

        ObjectData second = actualObjects.get(1);
        assertEquals("group2", second.getGroup());
        assertEquals("type2", second.getType());
        assertEquals(2, second.getNumber());
        assertEquals(20, second.getWeight());
    }

    @Test
    void testParseWithEmptyArray() throws IOException {
        String jsonData = "[]";
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write(jsonData);
        }
        List<ObjectData> actualObjects = jsonPars.parse(tempFile); // Use the instance to call parse
        assertNotNull(actualObjects);
        assertTrue(actualObjects.isEmpty());
    }

    @Test
    void testParseWithInvalidJson() {
        String jsonData = "{not an array}";
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write(jsonData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThrows(IOException.class, () -> jsonPars.parse(tempFile)); // Use the instance to call parse
    }
}