package com.example.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for User entity.
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should set and get id")
    void testId() {
        user.setId(1L);
        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get username")
    void testUsername() {
        user.setUsername("admin");
        assertThat(user.getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should set and get password")
    void testPassword() {
        user.setPassword("secret");
        assertThat(user.getPassword()).isEqualTo("secret");
    }

    @Test
    @DisplayName("Should set and get email")
    void testEmail() {
        user.setEmail("admin@test.com");
        assertThat(user.getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    @DisplayName("Should set and get enabled")
    void testEnabled() {
        user.setEnabled(true);
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should add role")
    void testAddRole() {
        user.addRole("ADMIN");
        assertThat(user.getRoles()).contains("ADMIN");
    }

    @Test
    @DisplayName("Should create user with constructor")
    void testConstructor() {
        User newUser = new User("testuser", "password123", "test@test.com");
        assertThat(newUser.getUsername()).isEqualTo("testuser");
        assertThat(newUser.getPassword()).isEqualTo("password123");
        assertThat(newUser.getEmail()).isEqualTo("test@test.com");
        assertThat(newUser.isEnabled()).isTrue();
    }
}
