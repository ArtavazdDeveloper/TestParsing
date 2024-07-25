package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class JsonPars implements Parser {
    private final int threadCount;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicInteger processedLines = new AtomicInteger(0);

    public JsonPars(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public List<ObjectData> parse(Path filePath) throws IOException, InterruptedException {
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        List<ObjectData> dataList;
        try {
            dataList = objectMapper.readValue(filePath.toFile(), objectMapper.getTypeFactory().constructCollectionType(List.class, ObjectData.class));
        } catch (IOException e) {
            System.out.println("Ошибка чтения JSON-файла: " + e.getMessage());
            throw e;
        }

        int totalLines = dataList.size();
        int partitionSize = totalLines / threadCount;
        int remainingLines = totalLines % threadCount;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int start = i * partitionSize;
            int end = (i == threadCount - 1) ? start + partitionSize + remainingLines : start + partitionSize;

            List<ObjectData> partition = dataList.subList(start, end);
            executorService.submit(() -> {
                processPartition(partition, totalLines);
                latch.countDown();
            });
        }

        latch.await();
        executorService.shutdown();
        return dataList;
    }

    private void processPartition(List<ObjectData> partition, int totalLines) {
        for (ObjectData object : partition) {
            // Обработка объекта
            processedLines.incrementAndGet();
            double progress = (double) processedLines.get() / totalLines * 100;
            System.out.printf("\rProcessing JSON: %.2f%%", progress);
        }
    }
}
