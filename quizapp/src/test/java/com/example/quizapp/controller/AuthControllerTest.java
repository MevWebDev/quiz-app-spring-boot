package com.example.quizapp.controller;

import com.example.quizapp.security.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for AuthController - login page.
 */
@WebMvcTest(AuthController.class)
@WithMockUser
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Should show login page")
    void loginPage_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle login with error parameter")
    void loginPage_ShouldHandleErrorParam() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle login with logout parameter")
    void loginPage_ShouldHandleLogoutParam() throws Exception {
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk());
    }
}
