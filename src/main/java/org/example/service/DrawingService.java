package org.example.service;

import org.example.domain.Notification;
import org.example.domain.Tree;
import org.example.repo.DrawingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class DrawingService {
    @Autowired
    private DrawingRepo drawingRepo;
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file,
                                              @RequestParam("parentPath") String parentPath,
                                              @RequestParam("imageName") String imageName,
                                              @RequestParam("usernames") List<String> usernames,
                                              @RequestParam("description") String description) {
        return drawingRepo.add(file,parentPath,imageName,usernames,description);
    }

    public ResponseEntity<Map<String, String>> getAllImages() {
        return drawingRepo.getAllImagesFromJsonRoots();
    }

    public ResponseEntity<Map<String, String>> getImagesFromRoot(@RequestParam String rootPath) {
        return drawingRepo.getImagesFromRoot(rootPath);
    }

    public ResponseEntity<Tree> getTreeStructure(@RequestParam String rootPath) {
        return drawingRepo.getTreeStructure(rootPath);
    }

    public ResponseEntity<List<Notification>> getNotifications(@RequestParam String username) {
        return drawingRepo.getNotifications(username);
    }

    public ResponseEntity<String> requestAccess(String drawingPath, String requester) {
        return drawingRepo.requestAccess(drawingPath, requester);
    }
    public void respondToRequest(String drawingName, String fromUser, boolean accept) throws Exception {
        drawingRepo.respondToRequest(drawingName, fromUser, accept);
    }

    }
