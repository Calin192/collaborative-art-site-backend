package org.example.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.Drawing;
import org.example.domain.Notification;
import org.example.domain.Tree;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static org.example.utils.AssetsUtils.countImagesInAssets;
import org.example.DrawingObservable;
import org.example.DrawingEvent;

@Repository
public class DrawingRepo {


    public ResponseEntity<String> add(
            @RequestParam("image") MultipartFile file,
            @RequestParam("parentPath") String parentPath,
            @RequestParam("imageName") String imageName,
            @RequestParam("usernames") List<String> usernames,
            @RequestParam("description") String description) {

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

            Path filepath = Paths.get(
                    "src/main/resources/assets",
                    filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, file.getBytes());

            Drawing drawing = new Drawing(imageName, usernames, description);
            drawing.setPendingRequests(new ArrayList<>());

            Path jsonFilePath = Paths.get("drawings.json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            List<Tree> trees = new ArrayList<>();
            if (Files.exists(jsonFilePath) && Files.size(jsonFilePath) > 0) {
                trees = objectMapper.readValue(
                        jsonFilePath.toFile(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Tree.class)
                );
            }

            // Create a new Tree node and set createdAt to the current time
            Tree newNode = new Tree(filename);
            newNode.setCreatedAt(LocalDateTime.now());
            newNode.setDrawing(drawing);

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

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFilePath.toFile(), trees);
            // notify observers that a drawing was created
            DrawingObservable.getInstance().notifyEvent(new DrawingEvent(DrawingEvent.EventType.CREATED, filename, imageName, null, null));

            return ResponseEntity.ok("Image uploaded successfully");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }


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
                return ResponseEntity.ok(new HashMap<>());
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            List<Tree> roots = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Tree>>() {});

            Map<String, String> images = new HashMap<>();

            for (Tree root : roots) {
                Path imagePath = Paths.get("src/main/resources/assets", root.getPath());

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
            throw new RuntimeException("Error while collecting images from the selected root", e);
        }
        return images;
    }

    private void collectImages(Tree node, Map<String, String> images) throws IOException {
        Path imagePath = Paths.get("src/main/resources/assets", node.getPath());
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
            Path jsonPath = Paths.get("drawings.json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            if (!Files.exists(jsonPath) || Files.size(jsonPath) == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<Tree> roots = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Tree>>() {});
            Tree selectedRoot = roots.stream()
                    .filter(root -> root.getPath().equals(rootPath))
                    .findFirst()
                    .orElse(null);

            if (selectedRoot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Map<String, String> images = new HashMap<>();
            collectImages(selectedRoot, images);

            return ResponseEntity.ok(images);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to retrieve images from root: " + e.getMessage()));
        }
    }

    public ResponseEntity<Tree> getTreeStructure(@RequestParam String rootPath) {
        try {
            Path jsonPath = Paths.get("drawings.json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            if (!Files.exists(jsonPath) || Files.size(jsonPath) == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    public ResponseEntity<String> requestAccess(String drawingPath, String requester) {
        try {
            Path jsonPath = Paths.get("drawings.json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            if (!Files.exists(jsonPath) || Files.size(jsonPath) == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No drawings available");
            }

            List<Tree> roots = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Tree>>() {});
            boolean found = false;

            for (Tree root : roots) {
                if (addPendingRequestToTree(root, drawingPath, requester)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Drawing not found");
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonPath.toFile(), roots);
            // notify observers that access was requested
            DrawingObservable.getInstance().notifyEvent(new DrawingEvent(DrawingEvent.EventType.REQUESTED, drawingPath, null, requester, null));
            return ResponseEntity.ok("Request sent");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while processing request");
        }
    }

    private boolean addPendingRequestToTree(Tree node, String drawingPath, String requester) {
        if (node.getPath().equals(drawingPath)) {
            Drawing drawing = node.getDrawing();
            if (drawing == null) return false;

            List<String> pending = drawing.getPendingRequests();
            if (pending == null) {
                pending = new ArrayList<>();
                drawing.setPendingRequests(pending);
            }

            if (!pending.contains(requester)) {
                pending.add(requester);
            }
            return true;
        }

        for (Tree child : node.getChildren()) {
            if (addPendingRequestToTree(child, drawingPath, requester)) {
                return true;
            }
        }

        return false;
    }



    public ResponseEntity<List<Notification>> getNotifications(@RequestParam String username) {
        try {
            Path jsonPath = Paths.get("drawings.json");
            if (!Files.exists(jsonPath) || Files.size(jsonPath) == 0) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            List<Tree> roots = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Tree>>() {});
            List<Notification> notifications = new ArrayList<>();

            for (Tree root : roots) {
                Drawing drawing = root.getDrawing();
                if (drawing != null) {
                    List<String> owners = drawing.getUsername(); // lista de useri care au acces
                    System.out.println("Owners for drawing " + drawing.getName() + ": " + owners);
                    if (owners != null && owners.contains(username)) { // dacă userul curent e owner
                        List<String> pendingRequests = drawing.getPendingRequests();
                        if (pendingRequests != null) {
                            for (String requester : pendingRequests) {
                                notifications.add(new Notification(drawing.getName(), requester));
                            }
                        }
                    }
                }
            }

            return ResponseEntity.ok(notifications);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    public void respondToRequest(String drawingName, String fromUser, boolean accept) throws Exception {
        Path jsonPath = Paths.get("drawings.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        List<Tree> roots = new ArrayList<>();
        if (Files.exists(jsonPath) && Files.size(jsonPath) > 0) {
            roots = mapper.readValue(jsonPath.toFile(), new TypeReference<List<Tree>>() {});
        }

        boolean updated = false;

        for (Tree root : roots) {
            if (updateDrawingInTree(root, drawingName, fromUser, accept)) {
                updated = true;
                break;
            }
        }

        if (updated) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonPath.toFile(), roots);
            // notify observers that a request was responded to
            DrawingObservable.getInstance().notifyEvent(new DrawingEvent(DrawingEvent.EventType.RESPONDED, null, drawingName, fromUser, accept));
        } else {
            throw new RuntimeException("Drawing not found");
        }
    }

    private boolean updateDrawingInTree(Tree node, String drawingName, String fromUser, boolean accept) {
        Drawing d = node.getDrawing();
        if (d != null && d.getName().equals(drawingName)) {
            d.getPendingRequests().remove(fromUser);
            if (accept && !d.getUsername().contains(fromUser)) {
                d.getUsername().add(fromUser);
            }
            return true;
        }

        for (Tree child : node.getChildren()) {
            if (updateDrawingInTree(child, drawingName, fromUser, accept)) {
                return true;
            }
        }

        return false;
    }

}
