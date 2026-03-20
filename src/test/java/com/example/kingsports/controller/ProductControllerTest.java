package com.example.kingsports.controller;

import com.example.kingsports.model.Category;
import com.example.kingsports.model.Product;
import com.example.kingsports.repository.CategoryRepository;
import com.example.kingsports.repository.ProductRepository;
import com.example.kingsports.repository.CartItemRepository;
import com.example.kingsports.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setName("路跑");
        category = categoryRepository.save(category);

        Product product = Product.builder()
                .name("Test Shoe")
                .description("A test running shoe")
                .price(new BigDecimal("1999.00"))
                .stock(10)
                .category(category)
                .imageUrl("http://example.com/test.jpg")
                .build();
        productRepository.save(product);
    }

    @Test
    public void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Shoe"))
                .andExpect(jsonPath("$[0].price").value(1999.00));
    }

    @Test
    public void testGetProductById() throws Exception {
        Product product = productRepository.findAll().get(0);
        mockMvc.perform(get("/api/products/" + product.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Shoe"));
    }

    @Test
    public void testGetProductsByCategory() throws Exception {
        Category category = categoryRepository.findAll().get(0);
        mockMvc.perform(get("/api/products/category/" + category.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Shoe"));
    }
}
