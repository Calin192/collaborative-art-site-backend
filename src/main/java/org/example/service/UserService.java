package org.example.service;

import org.example.domain.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public List<User> getUsers() {
        return userRepo.getUsers();
    }

    public void saveUsers(List<User> users) {
        userRepo.saveUsers(users);
    }

    public boolean registerUser(User newUser) {
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getUsername().equals(newUser.getUsername())) {
                return false;
            }
        }
        users.add(newUser);
        saveUsers(users);
        return true;
    }

    public boolean loginUser(String username, String password) {
        if (username == null || password == null) return false;

        List<User> users = getUsers();
        if (users == null || users.isEmpty()) return false;
        return users.stream()
                .anyMatch(user -> username.equals(user.getUsername()) && password.equals(user.getPassword()));
    }


}
