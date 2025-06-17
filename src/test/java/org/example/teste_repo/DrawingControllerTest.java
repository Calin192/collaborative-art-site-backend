package org.example.teste_repo;

import org.example.controller.DrawingController;
import org.example.domain.Tree;
import org.example.service.DrawingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DrawingController.class)
public class DrawingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DrawingService drawingService;

    @Test
    public void testUploadImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "dummy content".getBytes());

        Mockito.when(drawingService.uploadImage(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(ResponseEntity.ok("Image uploaded successfully"));

        Mockito.when(drawingService.uploadImage(
                        Mockito.any(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString()))
                .thenReturn(ResponseEntity.ok("Image uploaded successfully"));
    }

    @Test
    public void testGetAllImages() throws Exception {
        Mockito.when(drawingService.getAllImages())
                .thenReturn(ResponseEntity.ok(Map.of("img1", "path1", "img2", "path2")));

        mockMvc.perform(get("/getAllImages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.img1").value("path1"))
                .andExpect(jsonPath("$.img2").value("path2"));
    }

    @Test
    public void testGetImagesFromRoot() throws Exception {
        Mockito.when(drawingService.getImagesFromRoot("root"))
                .thenReturn(ResponseEntity.ok(Map.of("img3", "path3")));

        mockMvc.perform(get("/getImagesFromRoot").param("rootPath", "root"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.img3").value("path3"));
    }

    @Test
    public void testGetTreeStructure() throws Exception {
        Tree tree = new Tree(); // folosește o instanță mock sau reală
        Mockito.when(drawingService.getTreeStructure("root"))
                .thenReturn(ResponseEntity.ok(tree));

        mockMvc.perform(get("/getTreeStructure").param("rootPath", "root"))
                .andExpect(status().isOk());
    }
}
