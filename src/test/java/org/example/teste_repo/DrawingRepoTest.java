package org.example.teste_repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.Tree;
import org.example.repo.DrawingRepo;
import org.example.utils.AssetsUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.example.utils.AssetsUtils.countImagesInAssets;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DrawingRepoTest {

    private DrawingRepo drawingRepo;

    @BeforeEach
    public void setUp() {
        drawingRepo = new DrawingRepo();
    }

    @Test
    public void testAddImageSuccess() throws IOException {
        /*MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "drawing.png",
                "image/png",
                "dummy image content".getBytes()
        );

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class);
             MockedStatic<Paths> mockedPaths = mockStatic(Paths.class);
             MockedStatic<AssetsUtils> mockedAssets = mockStatic(AssetsUtils.class)) {

            mockedAssets.when(AssetsUtils::countImagesInAssets).thenReturn(1L);
            Path mockPath = mock(Path.class);
            mockedPaths.when(() -> Paths.get(anyString(), anyString(), anyString())).thenReturn(mockPath);
            mockedFiles.when(() -> Files.createDirectories(any())).thenReturn(mockPath);
            mockedFiles.when(() -> Files.write(any(), any(byte[].class))).thenReturn(mockPath);

            ResponseEntity<String> response = drawingRepo.add(mockFile, "parentPath");
            assertEquals(200, response.getStatusCodeValue());
            assertEquals("Image uploaded successfully", response.getBody());
        }*/
    }
    @Test
    void testAddImageFailure() throws IOException {
        MultipartFile mockFile = org.mockito.Mockito.mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.png");
        when(mockFile.getBytes()).thenReturn("fake image data".getBytes());


        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.createDirectories(any()))
                    .thenThrow(new IOException("Simulated IO Exception"));

            ResponseEntity<String> response = drawingRepo.add(mockFile, null,null,null,null);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Upload failed", response.getBody());
        }
    }

    @Test
    public void testGetImagesFromSelectedRoot() throws IOException {
        Tree root = new Tree("test.png");
        Tree child = new Tree("child.png");
        root.addChild(child);

        Path rootPath = Paths.get("src/main/resources/assets/test.png");
        Path childPath = Paths.get("src/main/resources/assets/child.png");

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class);
             MockedStatic<Paths> mockedPaths = mockStatic(Paths.class)) {

            mockedPaths.when(() -> Paths.get("src/main/resources/assets/", "test.png")).thenReturn(rootPath);
            mockedPaths.when(() -> Paths.get("src/main/resources/assets/", "child.png")).thenReturn(childPath);

            mockedFiles.when(() -> Files.exists(rootPath)).thenReturn(true);
            mockedFiles.when(() -> Files.exists(childPath)).thenReturn(true);
            mockedFiles.when(() -> Files.isRegularFile(rootPath)).thenReturn(true);
            mockedFiles.when(() -> Files.isRegularFile(childPath)).thenReturn(true);
            mockedFiles.when(() -> Files.readAllBytes(rootPath)).thenReturn("root".getBytes());
            mockedFiles.when(() -> Files.readAllBytes(childPath)).thenReturn("child".getBytes());

            Map<String, String> result = drawingRepo.getImagesFromSelectedRoot(root);
            assertEquals(2, result.size());
            assertTrue(result.containsKey("test.png"));
            assertTrue(result.containsKey("child.png"));
        }
    }

}
