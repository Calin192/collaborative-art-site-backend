package org.example.teste_repo;

import org.example.domain.User;
import org.example.repo.UserRepo;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepo userRepo;
    private UserService userService;

    @BeforeEach
    public void setUp() throws Exception {
        userRepo = mock(UserRepo.class);
        userService = new UserService();

        // bag mock-ul userRepo in userService folosind reflectie
        Field field = UserService.class.getDeclaredField("userRepo");
        field.setAccessible(true);
        field.set(userService, userRepo);
    }

    @Test
    public void testGetUsers() {
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(new User("user1", "pass1"));
        when(userRepo.getUsers()).thenReturn(mockUsers);

        List<User> result = userService.getUsers();

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
        verify(userRepo, times(1)).getUsers();
    }

    @Test
    public void testSaveUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("user2", "pass2"));

        userService.saveUsers(users);

        verify(userRepo, times(1)).saveUsers(users);
    }

    @Test
    public void testRegisterUserSuccess() {
        List<User> existingUsers = new ArrayList<>();
        existingUsers.add(new User("user1", "pass1"));
        when(userRepo.getUsers()).thenReturn(existingUsers);

        User newUser = new User("user2", "pass2");
        boolean result = userService.registerUser(newUser);

        assertTrue(result);
        // Verifica ca s-a apelat saveUsers cu lista care contine si utilizatorul nou
        verify(userRepo, times(1)).saveUsers(argThat(list -> list.contains(newUser) && list.size() == 2));
    }

    @Test
    public void testRegisterUserFail_UserExists() {
        List<User> existingUsers = new ArrayList<>();
        existingUsers.add(new User("user1", "pass1"));
        when(userRepo.getUsers()).thenReturn(existingUsers);

        User newUser = new User("user1", "newpass");
        boolean result = userService.registerUser(newUser);

        assertFalse(result);
        verify(userRepo, never()).saveUsers(any());
    }

    @Test
    public void testLoginUserSuccess() {
        List<User> users = new ArrayList<>();
        users.add(new User("user1", "pass1"));
        when(userRepo.getUsers()).thenReturn(users);

        boolean result = userService.loginUser("user1", "pass1");
        assertTrue(result);
    }

    @Test
    public void testLoginUserFail_WrongPassword() {
        List<User> users = new ArrayList<>();
        users.add(new User("user1", "pass1"));
        when(userRepo.getUsers()).thenReturn(users);

        boolean result = userService.loginUser("user1", "wrongpass");
        assertFalse(result);
    }

    @Test
    public void testLoginUserFail_NullInputs() {
        assertFalse(userService.loginUser(null, "pass"));
        assertFalse(userService.loginUser("user", null));
    }

    @Test
    public void testLoginUserFail_EmptyUserList() {
        when(userRepo.getUsers()).thenReturn(new ArrayList<>());

        boolean result = userService.loginUser("user1", "pass1");
        assertFalse(result);
    }
}
