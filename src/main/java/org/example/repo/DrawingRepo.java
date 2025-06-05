package org.example.repo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.example.utils.AssetsUtils.countImagesInAssets;

@Repository
public class DrawingRepo {

    public ResponseEntity<String> add(@RequestParam("image") MultipartFile file) {
        try {
            long nr = countImagesInAssets();

            String filename = null;
            String originalFilename = file.getOriginalFilename();
            if(originalFilename!=null){
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
                filename = baseName + nr + extension;
            }
            // Save the image to the specified path
            Path filepath = Paths.get("C:\\a.Programming\\Anul_3\\Licenta\\Proiect\\Backend\\Java_part_1\\src\\main\\resources\\assets", filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, file.getBytes());
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    //getting all images
    public ResponseEntity<Map<String, String>> getAllImages() {
        try {
            Path assetsPath = Paths.get("C:\\a.Programming\\Anul_3\\Licenta\\Proiect\\Backend\\Java_part_1\\src\\main\\resources\\assets");
            Map<String, String> images = new HashMap<>();

            Files.list(assetsPath)
                    .filter(Files::isRegularFile)
                    .filter(file -> {
                        String fileName = file.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
                    })
                    .forEach(file -> {
                        try {
                            byte[] fileBytes = Files.readAllBytes(file);
                            String base64Image = Base64.getEncoder().encodeToString(fileBytes);
                            images.put(file.getFileName().toString(), base64Image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            return ResponseEntity.ok(images);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
