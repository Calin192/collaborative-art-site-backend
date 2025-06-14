package org.example.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.Drawing;
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
import java.util.*;

import static org.example.utils.AssetsUtils.countImagesInAssets;

@Repository
public class DrawingRepo {


    public ResponseEntity<String> add(
            @RequestParam("image") MultipartFile file,
            @RequestParam("parentPath") String parentPath) {

        System.out.println("ParentPath primit: " + parentPath);

        try {
            long nr = countImagesInAssets();

            String filename = null;
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
                filename = baseName + nr + extension;
            }

            if (parentPath != null && (parentPath.contains("..") || parentPath.contains(":") || parentPath.contains("\\"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parent path");
            }

            // Salvează în folderul assets, NU în funcție de parentPath-ul logic
            Path filepath = Paths.get(
                    "C:\\a.Programming\\Anul_3\\Licenta\\Proiect\\Backend\\Java_part_1\\src\\main\\resources\\assets",
                    filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, file.getBytes());

            // Construiește obiectul drawing
            Drawing drawing = new Drawing("Image Name", "Username", "Description");

            // Încarcă arborele JSON
            Path jsonFilePath = Paths.get("C:\\a.Programming\\Anul_3\\Licenta\\Proiect\\Backend\\Java_part_1\\drawings.json");
            ObjectMapper objectMapper = new ObjectMapper();
            List<Tree> trees = new ArrayList<>();
            if (Files.exists(jsonFilePath) && Files.size(jsonFilePath) > 0) {
                trees = objectMapper.readValue(
                        jsonFilePath.toFile(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Tree.class)
                );
            } else {
                trees = new ArrayList<>();
            }

            // Creează nodul nou
            Tree newNode = new Tree(filename);
            newNode.setDrawing(drawing);

            // Caută nodul părinte în arbore
            if (parentPath != null && !parentPath.isEmpty()) {
                String[] pathParts = parentPath.split("/");
                String targetParent = pathParts[pathParts.length - 1]; // ex: image8.png

                boolean added = false;
                for (Tree tree : trees) {
                    if (addNodeToParent(tree, targetParent, newNode)) {
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parent not found in tree");
                }
            } else {
                trees.add(newNode);
            }

            // Scrie JSON-ul înapoi
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFilePath.toFile(), trees);

            return ResponseEntity.ok("Image uploaded successfully");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }


    // Helper method to add a node to the correct parent
    private boolean addNodeToParent(Tree current, String targetPath, Tree newNode) {
        if (current.getPath().equals(targetPath)) {
            current.getChildren().add(newNode);
            return true;
        }
        for (Tree child : current.getChildren()) {
            if (addNodeToParent(child, targetPath, newNode)) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<Map<String, String>> getAllImagesFromJsonRoots() {
        try {
            Path jsonPath = Paths.get("drawings.json");

            if (!Files.exists(jsonPath) || Files.size(jsonPath) == 0) {
                // Dacă fișierul nu există sau e gol, returnează map gol
                return ResponseEntity.ok(new HashMap<>());
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Tree> roots = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Tree>>() {});

            Map<String, String> images = new HashMap<>();

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

            // 1. Încarca toate radacinile din JSON
            Path jsonPath = Paths.get("drawings.json");
            ObjectMapper mapper = new ObjectMapper();
            List<Tree> roots = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Tree>>() {});

            // 2. Gaseste root-ul cu path-ul specificat
            Tree selectedRoot = roots.stream()
                    .filter(root -> root.getPath().equals(rootPath))
                    .findFirst()
                    .orElse(null);

            if (selectedRoot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // 3. Parcurge arborele si extrage imaginile
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
