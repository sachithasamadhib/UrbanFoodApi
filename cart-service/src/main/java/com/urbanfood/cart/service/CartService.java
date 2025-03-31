package com.urbanfood.cart.service;

import com.urbanfood.cart.dto.CartDTO;
import com.urbanfood.cart.dto.CartRequestDTO;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    CartDTO addItemToCart(Long userId, CartRequestDTO request);
    CartDTO updateCartItem(Long userId, Long productId, CartRequestDTO request);
    CartDTO removeItemFromCart(Long userId, Long productId);
    void clearCart(Long userId);
}