package com.urbanfood.cart.service;

import com.urbanfood.cart.dto.CartRequestDTO;
import com.urbanfood.cart.dto.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCartByUserId(Long userId);
    CartResponseDTO addItemToCart(Long userId, CartRequestDTO request);
    CartResponseDTO updateCartItem(Long userId, Long productId, CartRequestDTO request);
    CartResponseDTO removeItemFromCart(Long userId, Long productId);
    CartResponseDTO clearCart(Long userId);
}