package com.urbanfood.cart.service;

import com.urbanfood.cart.dto.CartDTO;
import com.urbanfood.cart.dto.ResponseDTO;

public interface CartService {
    ResponseDTO<CartDTO> getCart(Long userId);
    ResponseDTO<CartDTO> addToCart(Long userId, Long productId, Integer quantity);
    ResponseDTO<CartDTO> updateCartItem(Long userId, Long productId, Integer quantity);
    ResponseDTO<CartDTO> removeFromCart(Long userId, Long productId);
    ResponseDTO<CartDTO> clearCart(Long userId);
}