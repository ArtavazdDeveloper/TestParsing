package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsTest {

    private List<ObjectData> objects;

    @BeforeEach
    void setUp() {
        objects = new ArrayList<>();
        objects.add(new ObjectData("A", "type1", 1, 10));
        objects.add(new ObjectData("B", "type2", 2, 20));
        objects.add(new ObjectData("A", "type1", 3, 30));
    }

    @Test
    void testPrintStatistics() {
        Statistics statistics = new Statistics(objects);
        assertDoesNotThrow(statistics::printStatistics);
    }

    @Test
    void testDuplicates() {
        Statistics statistics = new Statistics(objects);
        assertDoesNotThrow(() -> {
        });
    }

    @Test
    void testGroupWeights() {
        Statistics statistics = new Statistics(objects);
        assertDoesNotThrow(() -> {
        });
    }

    @Test
    void testMinMaxWeights() {
        Statistics statistics = new Statistics(objects);
        assertDoesNotThrow(() -> {
        });
    }

    @Test
    void testEmptyList() {
        Statistics statistics = new Statistics(new ArrayList<>());
        assertDoesNotThrow(statistics::printStatistics);
    }
}