package org.example.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class AssetsUtils {
    public static long countImagesInAssets() {
        // Use a repository-relative path so it works on other machines/CI
        Path assetsPath = Paths.get("src", "main", "resources", "assets");
        try {
            if (!Files.exists(assetsPath)) {
                // Create the directory if it's missing to avoid NoSuchFileException later
                Files.createDirectories(assetsPath);
                return 0L;
            }

            try (Stream<Path> files = Files.list(assetsPath)) {
                return files.filter(Files::isRegularFile)
                        .filter(file -> {
                            String fileName = file.getFileName().toString().toLowerCase();
                            return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
                        })
                        .count();
            }
        } catch (IOException e) {
            // Log a concise message instead of a full stack trace in normal operation
            System.err.println("Unable to count images in assets: " + e.getMessage());
            return 0L;
        }
    }
}
