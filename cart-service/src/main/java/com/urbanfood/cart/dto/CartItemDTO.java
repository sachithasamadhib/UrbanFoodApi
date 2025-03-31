package com.urbanfood.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private Long productId;
    private String productName; // Will be populated from product service
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime addedDate;
    private BigDecimal subtotal;

    public BigDecimal calculateSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}