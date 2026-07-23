package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.domain.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = "*") // Allow frontend requests
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.registerUser(user) ? "User registered successfully" : "Username already exists";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user, HttpSession session) {
        System.out.println("Attempting to log in user: " + user.getUsername());
        boolean loginSuccessful = userService.loginUser(user.getUsername(), user.getPassword());
        if (loginSuccessful) {
            session.setAttribute("user", user.getUsername());
            return ResponseEntity.ok("Login successful");
        } else {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    @GetMapping("/profile")
    public String profile(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "You must log in first.";
        }
        return "Welcome, " + user;
    }

}
