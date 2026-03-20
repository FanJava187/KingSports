package com.example.kingsports.controller;

import com.example.kingsports.dto.OrderRequest;
import com.example.kingsports.model.Order;
import com.example.kingsports.model.User;
import com.example.kingsports.service.OrderService;
import com.example.kingsports.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@Valid @RequestBody OrderRequest request, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());
            Order order = orderService.checkout(user, request);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<Order> getMyOrders(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return orderService.getUserOrders(user);
    }
}
