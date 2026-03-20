package com.example.kingsports.controller;

import com.example.kingsports.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.kingsports.KingsportsApplication;
// ... (其他匯入)

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    static {
        KingsportsApplication.loadEnv();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Runner001");
        request.setEmail("runner@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registration successful. Please login."));
    }

    @Test
    public void testRegister_ValidationError_UsernameTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("R1");
        request.setEmail("valid@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username length must be between 3 and 20 characters"));
    }

    @Test
    public void testRegister_ValidationError_InvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Runner002");
        request.setEmail("invalid-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email format is incorrect"));
    }

    @Test
    public void testRegister_ValidationError_PasswordTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Runner003");
        request.setEmail("runner003@example.com");
        request.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password must be at least 6 characters long"));
    }

    @Test
    public void testRegister_DuplicateUsername() throws Exception {
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("duplicate_user");
        request1.setEmail("user1@example.com");
        request1.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("duplicate_user");
        request2.setEmail("user2@example.com");
        request2.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This username is already taken"));
    }
}
