package com.example.kingsports.controller;

import com.example.kingsports.dto.LoginRequest;
import com.example.kingsports.dto.OrderRequest;
import com.example.kingsports.model.*;
import com.example.kingsports.repository.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;
    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() throws Exception {
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // 1. 建立測試用戶
        testUser = User.builder()
                .username("testrunner")
                .email("test@kingsports.com")
                .password(passwordEncoder.encode("password123"))
                .role("USER")
                .build();
        userRepository.save(testUser);

        // 2. 建立測試商品與分類
        Category category = new Category();
        category.setName("路跑");
        category = categoryRepository.save(category);

        testProduct = Product.builder()
                .name("Speed Shoe")
                .price(new BigDecimal("1000.00"))
                .stock(10)
                .category(category)
                .build();
        testProduct = productRepository.save(testProduct);

        // 3. 取得 JWT Token (登入)
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testrunner");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        token = "Bearer " + objectMapper.readTree(responseContent).get("token").asText();
    }

    @Test
    public void testCheckout_Success() throws Exception {
        // A. 先加入購物車
        CartItem cartItem = CartItem.builder()
                .user(testUser)
                .product(testProduct)
                .quantity(2)
                .build();
        cartItemRepository.save(cartItem);

        // B. 執行結帳
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setShippingAddress("台北市大安區和平東路一段 1 號");

        mockMvc.perform(post("/api/orders/checkout")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(2000.00))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // C. 驗證資料庫變更
        // 1. 檢查庫存 (原本 10, 買了 2, 應剩 8)
        Product updatedProduct = productRepository.findById(testProduct.getId()).get();
        assertEquals(8, updatedProduct.getStock());

        // 2. 檢查購物車是否已清空
        assertTrue(cartItemRepository.findByUser(testUser).isEmpty());

        // 3. 檢查訂單是否產生
        assertEquals(1, orderRepository.findByUserOrderByCreatedAtDesc(testUser).size());
    }

    @Test
    public void testCheckout_EmptyCart() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setShippingAddress("Some Address");

        mockMvc.perform(post("/api/orders/checkout")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cart is empty, cannot checkout."));
    }

    @Test
    public void testCheckout_InsufficientStock() throws Exception {
        // A. 加入購物車 (數量大於庫存 10)
        CartItem cartItem = CartItem.builder()
                .user(testUser)
                .product(testProduct)
                .quantity(15) // 超過庫存
                .build();
        cartItemRepository.save(cartItem);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setShippingAddress("Some Address");

        mockMvc.perform(post("/api/orders/checkout")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product Speed Shoe is out of stock."));
    }

    @Test
    public void testGetMyOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }
}
