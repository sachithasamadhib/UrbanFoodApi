package com.urbanfood.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String productImage;
    private Double price;
    private Double shippingCost;
    private Integer quantity;
    private Double itemTotal;
    private LocalDateTime addedAt;
    private LocalDateTime expiresAt;
}