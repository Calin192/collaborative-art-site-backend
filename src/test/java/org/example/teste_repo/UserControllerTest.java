package org.example.teste_repo;

import org.example.controller.UserController;
import org.example.domain.User;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testRegisterSuccess() throws Exception {
        Mockito.when(userService.registerUser(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"testpass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testRegisterFailure() throws Exception {
        Mockito.when(userService.registerUser(any(User.class))).thenReturn(false);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"existing\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        Mockito.when(userService.loginUser(eq("user1"), eq("pass1"))).thenReturn(true);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user1\",\"password\":\"pass1\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        Mockito.when(userService.loginUser(eq("user1"), eq("wrongpass"))).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user1\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    public void testProfileWithoutLogin() throws Exception {
        mockMvc.perform(get("/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(content().string("You must log in first."));
    }

    @Test
    public void testProfileWithLogin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", "testuser");

        mockMvc.perform(get("/auth/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome, testuser"));
    }
}
