package org.example.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.Tree;
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
import java.util.List;
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
    public ResponseEntity<Map<String, String>> getAllImagesFromJsonRoots() {
        try {
            // Citește JSON-ul cu arborele (presupunem calea fișierului)
            Path jsonPath = Paths.get("drawings.json");
            ObjectMapper mapper = new ObjectMapper();

            // Deserializează JSON-ul într-o listă de Tree (root nodes)
            List<Tree> roots = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Tree>>() {});

            Map<String, String> images = new HashMap<>();

            // Pentru fiecare nod root, încarcă imaginea aferentă (presupunem că path-ul este numele imaginii)
            for (Tree root : roots) {
                String imagePathStr = "src/main/resources/assets/" + root.getPath();
                Path imagePath = Paths.get(imagePathStr);

                if (Files.exists(imagePath) && Files.isRegularFile(imagePath)) {
                    byte[] bytes = Files.readAllBytes(imagePath);
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    images.put(root.getPath(), base64);
                }
            }

            return ResponseEntity.ok(images);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    public Map<String, String> getImagesFromSelectedRoot(Tree selectedRoot) {
        Map<String, String> images = new HashMap<>();
        try {
            collectImages(selectedRoot, images);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }

    // Funcția recursivă folosită anterior
    private void collectImages(Tree node, Map<String, String> images) throws IOException {
        Path imagePath = Paths.get("src/main/resources/assets/", node.getPath());
        if (Files.exists(imagePath) && Files.isRegularFile(imagePath)) {
            byte[] bytes = Files.readAllBytes(imagePath);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            images.put(node.getPath(), base64);
        }
        for (Tree child : node.getChildren()) {
            collectImages(child, images);
        }
    }


    public ResponseEntity<Map<String, String>> getImagesFromRoot(@RequestParam String rootPath) {
        try {

            // 1. Încarcă toate rădăcinile din JSON
            Path jsonPath = Paths.get("drawings.json");
            ObjectMapper mapper = new ObjectMapper();
            List<Tree> roots = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Tree>>() {});

            // 2. Găsește root-ul cu path-ul specificat
            Tree selectedRoot = roots.stream()
                    .filter(root -> root.getPath().equals(rootPath))
                    .findFirst()
                    .orElse(null);

            if (selectedRoot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // 3. Parcurge arborele și extrage imaginile
            Map<String, String> images = new HashMap<>();
            collectImages(selectedRoot, images);

            return ResponseEntity.ok(images);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    public ResponseEntity<Tree> getTreeStructure(@RequestParam String rootPath) {
        try {
            Path jsonPath = Paths.get("drawings.json");
            ObjectMapper mapper = new ObjectMapper();
            List<Tree> roots = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Tree>>() {});

            Tree selectedRoot = roots.stream()
                    .filter(root -> root.getPath().equals(rootPath))
                    .findFirst()
                    .orElse(null);

            if (selectedRoot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok(selectedRoot);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
