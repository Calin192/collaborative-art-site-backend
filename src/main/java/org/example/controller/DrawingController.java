package org.example.controller;

import org.example.domain.Notification;
import org.example.domain.Tree;
import org.example.service.DrawingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class DrawingController {
    @Autowired
    private DrawingService drawingService;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file,
                                              @RequestParam("parentPath") String parentPath,
                                              @RequestParam("imageName") String imageName,
                                              @RequestParam("usernames") List<String> usernames,
                                              @RequestParam("description") String description) {
        return drawingService.uploadImage(file, parentPath, imageName, usernames, description);
    }

    @GetMapping("/getAllImages")
    public ResponseEntity<Map<String, String>> getAllImages() {
        return drawingService.getAllImages();
    }

    @GetMapping("/getImagesFromRoot")
    public ResponseEntity<Map<String, String>> getImagesFromRoot(@RequestParam String rootPath) {
        return drawingService.getImagesFromRoot(rootPath);
    }

    @GetMapping("/getTreeStructure")
    public ResponseEntity<Tree> getTreeStructure(@RequestParam String rootPath) {
        return drawingService.getTreeStructure(rootPath);
    }

    @PostMapping("/requestAccess")
    public ResponseEntity<String> requestAccess(
            @RequestParam("drawingPath") String drawingPath,
            @RequestParam("fromUser") String requester
    ) {
        System.out.println("Requesting access for drawing: " + drawingPath + " by requester: " + requester);
        return drawingService.requestAccess(drawingPath, requester);
    }


    @GetMapping("/requests")
    public ResponseEntity<List<Notification>> getNotifications(@RequestParam String username) {
        System.out.println("Fetching notifications for user: " + username);
        return drawingService.getNotifications(username);
    }

    @PostMapping("/respondToRequest")
    public void respondToRequest(String drawingName, String fromUser, boolean accept) throws Exception {
        System.out.println("Responding to request for drawing: " + drawingName + " from user: " + fromUser + " with accept: " + accept);
        drawingService.respondToRequest(drawingName, fromUser, accept);
    }
    }
