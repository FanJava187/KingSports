package com.example.kingsports.controller;

import com.example.kingsports.KingsportsApplication;
import com.example.kingsports.dto.LoginRequest;
import com.example.kingsports.dto.ProductRequest;
import com.example.kingsports.model.Category;
import com.example.kingsports.model.User;
import com.example.kingsports.repository.CategoryRepository;
import com.example.kingsports.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;
// ... (其他匯入)

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminProductTest {

    static {
        KingsportsApplication.loadEnv();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private Long categoryId;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        User admin = User.builder()
                .username("admin_user")
                .email("admin@kingsports.com")
                .password(passwordEncoder.encode("admin123"))
                .role("ADMIN")
                .build();
        userRepository.save(admin);

        Category category = new Category();
        category.setName("Running");
        category = categoryRepository.save(category);
        this.categoryId = category.getId();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin_user");
        loginRequest.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        this.adminToken = "Bearer " + objectMapper.readTree(responseContent).get("token").asText();
    }

    @Test
    public void testAdminCanCreateProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Pro Admin Shoe");
        request.setPrice(new BigDecimal("5000"));
        request.setStock(10);
        request.setCategoryId(this.categoryId);

        mockMvc.perform(post("/api/products")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
