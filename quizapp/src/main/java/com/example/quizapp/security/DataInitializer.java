package com.example.quizapp.security;

import com.example.quizapp.entity.User;
import com.example.quizapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Initializes default users on application startup.
 * Uses BCryptPasswordEncoder to properly encode passwords.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create or update admin user
        createOrUpdateUser("admin", "admin123", "admin@quizapp.com", "ADMIN", "USER");
        
        // Create or update regular user
        createOrUpdateUser("user", "user123", "user@quizapp.com", "USER");
    }

    private void createOrUpdateUser(String username, String password, String email, String... roles) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        
        User user;
        if (existingUser.isPresent()) {
            // Update existing user password
            user = existingUser.get();
            user.setPassword(passwordEncoder.encode(password));
            user.getRoles().clear();
            System.out.println("Updated password for user: " + username);
        } else {
            // Create new user
            user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setEnabled(true);
            System.out.println("Created new user: " + username);
        }
        
        for (String role : roles) {
            user.addRole(role);
        }
        
        userRepository.save(user);
        System.out.println("User " + username + " ready with password: " + password);
    }
}
