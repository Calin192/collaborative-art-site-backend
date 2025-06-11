package org.example.teste_repo;

import org.example.domain.Tree;
import org.example.repo.DrawingRepo;
import org.example.service.DrawingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DrawingServiceTest {

    private DrawingRepo drawingRepo;
    private DrawingService drawingService;



    @BeforeEach
    public void setUp() throws Exception {
        drawingRepo = mock(DrawingRepo.class);
        drawingService = new DrawingService();

        // Inject mock using reflection
        Field field = DrawingService.class.getDeclaredField("drawingRepo");
        field.setAccessible(true);
        field.set(drawingService, drawingRepo);
    }


    @Test
    public void testUploadImage() {
        MultipartFile mockFile = mock(MultipartFile.class);
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Image uploaded");

        when(drawingRepo.add(mockFile)).thenReturn(mockResponse);

        ResponseEntity<String> response = drawingService.uploadImage(mockFile);

        assertEquals("Image uploaded", response.getBody());
        verify(drawingRepo, times(1)).add(mockFile);
    }

    @Test
    public void testGetAllImages() {
        Map<String, String> images = new HashMap<>();
        images.put("image1.png", "base64string");
        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.ok(images);

        when(drawingRepo.getAllImagesFromJsonRoots()).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = drawingService.getAllImages();

        assertEquals(images, response.getBody());
        verify(drawingRepo, times(1)).getAllImagesFromJsonRoots();
    }

    @Test
    public void testGetImagesFromRoot() {
        String rootPath = "rootPath";
        Map<String, String> images = new HashMap<>();
        images.put("image2.png", "base64string2");
        ResponseEntity<Map<String, String>> mockResponse = ResponseEntity.ok(images);

        when(drawingRepo.getImagesFromRoot(rootPath)).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = drawingService.getImagesFromRoot(rootPath);

        assertEquals(images, response.getBody());
        verify(drawingRepo, times(1)).getImagesFromRoot(rootPath);
    }

    @Test
    public void testGetTreeStructure() {
        String rootPath = "rootPath";
        Tree tree = new Tree(rootPath);
        ResponseEntity<Tree> mockResponse = ResponseEntity.ok(tree);

        when(drawingRepo.getTreeStructure(rootPath)).thenReturn(mockResponse);

        ResponseEntity<Tree> response = drawingService.getTreeStructure(rootPath);

        assertEquals(tree, response.getBody());
        verify(drawingRepo, times(1)).getTreeStructure(rootPath);
    }
}
