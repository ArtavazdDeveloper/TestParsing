package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Statistics {
    private final List<ObjectData> objects;

    public Statistics(List<ObjectData> objects) {
        this.objects = objects;
    }

    public void printStatistics() {
        printDuplicates();
        printGroupWeights();
        printMinMaxWeights();
    }

    private void printDuplicates() {
        Map<ObjectData, Long> duplicates = objects.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        System.out.println();
        System.out.println("Duplicates:");
        duplicates.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> System.out.printf("%s - %d times%n", entry.getKey(), entry.getValue()));
    }

    private void printGroupWeights() {
        Map<String, Long> groupWeights = new HashMap<>();
        for (ObjectData obj : objects) {
            groupWeights.put(obj.getGroup(), groupWeights.getOrDefault(obj.getGroup(), 0L) + obj.getWeight());
        }

        System.out.println("Group weights:");
        groupWeights.forEach((group, weight) -> System.out.println(group + ": " + weight));
    }

    private void printMinMaxWeights() {
        long minWeight = objects.stream().mapToLong(ObjectData::getWeight).min().orElse(0);
        long maxWeight = objects.stream().mapToLong(ObjectData::getWeight).max().orElse(0);

        System.out.println("Min weight: " + minWeight);
        System.out.println("Max weight: " + maxWeight);
    }
}