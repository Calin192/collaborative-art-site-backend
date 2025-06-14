package org.example.controller;

import org.example.domain.Tree;
import org.example.service.DrawingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class DrawingController {
    @Autowired
    private DrawingService drawingService;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file, @RequestParam String parentPath) {
        return drawingService.uploadImage(file, parentPath);
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


}
