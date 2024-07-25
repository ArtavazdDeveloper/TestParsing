package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Parser {
    List<ObjectData> parse(Path filePath) throws IOException, InterruptedException;
}
