package com.urbanfood.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long cartId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private List<CartItemDTO> items = new ArrayList<>();
    private Double totalPrice;
    private Double totalShippingCost;
    private Double grandTotal;
}