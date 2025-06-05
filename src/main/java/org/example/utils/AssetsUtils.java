package org.example.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class AssetsUtils {
    public static long countImagesInAssets() {
        Path assetsPath = Paths.get("C:\\a.Programming\\Anul_3\\Licenta\\Proiect\\Backend\\Java_part_1\\src\\main\\resources\\assets");
        try (Stream<Path> files = Files.list(assetsPath)) {
            return files.filter(Files::isRegularFile)
                    .filter(file -> {
                        String fileName = file.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
                    })
                    .count();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
