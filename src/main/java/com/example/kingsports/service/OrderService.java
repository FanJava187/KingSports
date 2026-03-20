package com.example.kingsports.service;

import com.example.kingsports.dto.OrderRequest;
import com.example.kingsports.model.*;
import com.example.kingsports.repository.CartItemRepository;
import com.example.kingsports.repository.OrderRepository;
import com.example.kingsports.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order checkout(User user, OrderRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty, cannot checkout.");
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 1. Create order main table
        Order order = Order.builder()
                .user(user)
                .status("PENDING")
                .shippingAddress(request.getShippingAddress())
                .createdAt(LocalDateTime.now())
                .build();

        // 2. Process each cart item
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            
            // Check stock
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Product " + product.getName() + " is out of stock.");
            }

            // Deduct stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // Create order details
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();
            
            orderItems.add(orderItem);
            
            // Calculate total price
            totalPrice = totalPrice.add(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);

        // 3. Save order
        Order savedOrder = orderRepository.save(order);

        // 4. Clear cart
        cartItemRepository.deleteByUser(user);

        return savedOrder;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
