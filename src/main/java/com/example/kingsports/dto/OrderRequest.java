package com.example.kingsports.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank(message = "Shipping address cannot be empty")
    private String shippingAddress;
}
