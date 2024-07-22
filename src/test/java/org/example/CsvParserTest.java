package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvParserTest {

    private Path tempFile;
    private CsvParser csvParser; // Add an instance variable for CsvParser

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".csv");
        csvParser = new CsvParser(); // Initialize the instance
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testParse() throws IOException {
        String csvData = "group,type,number,weight\n" +
                "A,type1,1,10\n" +
                "B,type2,2,20\n" +
                "C,type3,3,30";
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write(csvData);
        }
        List<ObjectData> actualObjects = csvParser.parse(tempFile); // Use the instance to call parse
        assertNotNull(actualObjects);
        assertEquals(3, actualObjects.size());
        assertEquals("A", actualObjects.get(0).getGroup());
        assertEquals(10, actualObjects.get(0).getWeight());
    }

    @Test
    void parseValidFile() throws IOException {
        String csvData = "group,type,number,weight\n" +
                "A,type1,1,10\n" +
                "B,type2,2,20\n" +
                "C,type3,3,30";
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write(csvData);
        }
        assertDoesNotThrow(() -> csvParser.parse(tempFile)); // Use the instance to call parse
    }

    @Test
    void parseMalformedFile() {
        String csvData = "group,type,number,weight\n" +
                "A,type1,one,10"; // Malformed number field
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write(csvData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThrows(IOException.class, () -> csvParser.parse(tempFile)); // Use the instance to call parse
    }
}