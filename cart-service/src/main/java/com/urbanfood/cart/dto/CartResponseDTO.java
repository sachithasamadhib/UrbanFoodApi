package com.urbanfood.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private CartDTO cart;
    private String message;
    private boolean success;

    public static CartResponseDTO success(CartDTO cart, String message) {
        return new CartResponseDTO(cart, message, true);
    }

    public static CartResponseDTO error(String message) {
        return new CartResponseDTO(null, message, false);
    }
}