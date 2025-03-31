package com.urbanfood.cart.service;

import com.urbanfood.cart.dto.CartDTO;
import com.urbanfood.cart.dto.CartItemDTO;
import com.urbanfood.cart.dto.CartRequestDTO;
import com.urbanfood.cart.exception.CartException;
import com.urbanfood.cart.model.Cart;
import com.urbanfood.cart.model.CartItem;
import com.urbanfood.cart.repository.CartItemRepository;
import com.urbanfood.cart.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Create empty cart if not exists
                    Cart newCart = new Cart();
                    newCart.setId(cartRepository.getNextCartId());
                    newCart.setUserId(userId);
                    newCart.setCreatedDate(LocalDateTime.now());
                    newCart.setUpdatedDate(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        return convertToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(Long userId, CartRequestDTO request) {
        try {
            // Call Oracle stored procedure
            cartRepository.callAddToCartProcedure(
                    userId,
                    request.getProductId(),
                    request.getQuantity(),
                    request.getPrice()
            );

            // Return updated cart
            return getCartByUserId(userId);
        } catch (Exception e) {
            log.error("Error adding item to cart", e);
            throw new CartException("Failed to add item to cart: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long userId, Long productId, CartRequestDTO request) {
        try {
            // Call Oracle stored procedure
            cartRepository.callUpdateCartQuantityProcedure(
                    userId,
                    productId,
                    request.getQuantity()
            );

            // Return updated cart
            return getCartByUserId(userId);
        } catch (Exception e) {
            log.error("Error updating cart item", e);
            throw new CartException("Failed to update cart item: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CartDTO removeItemFromCart(Long userId, Long productId) {
        try {
            // Call Oracle stored procedure
            cartRepository.callRemoveFromCartProcedure(userId, productId);

            // Return updated cart
            return getCartByUserId(userId);
        } catch (Exception e) {
            log.error("Error removing item from cart", e);
            throw new CartException("Failed to remove item from cart: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        try {
            // Call Oracle stored procedure
            cartRepository.callClearCartProcedure(userId);
        } catch (Exception e) {
            log.error("Error clearing cart", e);
            throw new CartException("Failed to clear cart: " + e.getMessage());
        }
    }

    private CartDTO convertToCartDTO(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        List<CartItemDTO> cartItemDTOs = cartItems.stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());

        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getId());
        cartDTO.setUserId(cart.getUserId());
        cartDTO.setCreatedDate(cart.getCreatedDate());
        cartDTO.setUpdatedDate(cart.getUpdatedDate());
        cartDTO.setItems(cartItemDTOs);
        cartDTO.setTotalPrice(cartDTO.calculateTotalPrice());

        return cartDTO;
    }

    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setCartItemId(cartItem.getId());
        dto.setProductId(cartItem.getProductId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getPrice());
        dto.setAddedDate(cartItem.getAddedDate());
        dto.setSubtotal(dto.calculateSubtotal());

        // In a real application, you would call the product service to get the product name
        dto.setProductName("Product " + cartItem.getProductId());

        return dto;
    }
}