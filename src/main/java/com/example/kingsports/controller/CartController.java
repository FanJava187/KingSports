package com.example.kingsports.controller;

import com.example.kingsports.dto.CartRequest;
import com.example.kingsports.model.CartItem;
import com.example.kingsports.model.User;
import com.example.kingsports.service.CartService;
import com.example.kingsports.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<CartItem> getCart(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return cartService.getCartItems(user);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartRequest request, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        cartService.addToCart(user, request);
        return ResponseEntity.ok("Item added to cart.");
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return ResponseEntity.ok("Item removed from cart.");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        cartService.clearCart(user);
        return ResponseEntity.ok("Cart cleared.");
    }
}
