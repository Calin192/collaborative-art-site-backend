package org.example.service;

import org.example.repo.DrawingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class DrawingService {
    @Autowired
    private DrawingRepo drawingRepo;
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        return drawingRepo.add(file);
    }

    public ResponseEntity<Map<String, String>> getAllImages() {
        return drawingRepo.getAllImages();
    }
}
