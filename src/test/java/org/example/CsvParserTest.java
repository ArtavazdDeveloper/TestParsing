package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvParserTest {
    private Path testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = Files.createTempFile("test", ".csv");
        try (CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(testFile), CSVFormat.DEFAULT.withHeader("group", "type", "number", "weight"))) {
            printer.printRecord("A", "type1", 1, 10);
            printer.printRecord("B", "type2", 2, 20);
            printer.printRecord("A", "type1", 3, 30);
        }
    }

    @Test
    void testParse() throws IOException {
        List<ObjectData> objects = CsvParser.parse(testFile);
        assertEquals(3, objects.size());
        assertEquals("A", objects.get(0).getGroup());
        assertEquals(10, objects.get(0).getWeight());
    }

    @Test
    void testEmptyFile() throws IOException {
        Path emptyFile = Files.createTempFile("empty", ".csv");
        List<ObjectData> objects = CsvParser.parse(emptyFile);
        assertTrue(objects.isEmpty());
    }

    @Test
    void testMalformedCsv() {
        Path malformedFile;
        try {
            malformedFile = Files.createTempFile("malformed", ".csv");
            Files.write(malformedFile, "group,type,number,weight\nA,type1,1\nB,type2\n".getBytes());
            assertThrows(IllegalArgumentException.class, () -> CsvParser.parse(malformedFile));
        } catch (IOException e) {
            fail("Failed to create or test an incorrectly formatted CSV file: " + e.getMessage());
        }
    }
}
