package org.example.teste_repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.User;
import org.example.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepoTest {

    @TempDir
    Path tempDir;

    private File tempFile;
    private UserRepo repo;

    @BeforeEach
    void setUp() {
        tempFile = tempDir.resolve("users.json").toFile();
        repo = new UserRepo() {
            // am suprascris calea fisierului cu cea temporara
            @Override
            public List<User> getUsers() {
                try {
                    if (!tempFile.exists()) {
                        return List.of();
                    }
                    return new ObjectMapper().readValue(tempFile, new com.fasterxml.jackson.core.type.TypeReference<>() {});
                } catch (IOException e) {
                    return List.of();
                }
            }

            @Override
            public void saveUsers(List<User> users) {
                try {
                    new ObjectMapper().writeValue(tempFile, users);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Test
    void testGetUsers_FileNotExists_ReturnsEmptyList() {
        assertFalse(tempFile.exists());
        List<User> users = repo.getUsers();
        assertTrue(users.isEmpty(), "Fișierul nu există -> lista trebuie să fie goală");
    }

    @Test
    void testSaveUsers_AndGetUsers() {
        List<User> expected = List.of(
                new User("ana", "123"),
                new User("bob", "abc")
        );

        repo.saveUsers(expected);

        List<User> actual = repo.getUsers();
        assertEquals(2, actual.size());
        assertEquals("ana", actual.get(0).getUsername());
        assertEquals("123", actual.get(0).getPassword());
        assertEquals("bob", actual.get(1).getUsername());
    }

    @Test
    void testGetUsers_InvalidJson_ReturnsEmptyList() throws IOException {
        java.nio.file.Files.writeString(tempFile.toPath(), "invalid json content");

        List<User> users = repo.getUsers();
        assertTrue(users.isEmpty(), "JSON invalid -> lista trebuie să fie goală");
    }
}
