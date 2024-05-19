package com.tritonkor.domain.service.impl;

import com.tritonkor.domain.exception.ImageNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    public Path getPathFromResource(String resourceName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        Path tempFile = Files.createTempFile("temp", null);
        try (InputStream inputStream = classLoader.getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourceName);
            }
            Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }

    public byte[] getBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }
}
