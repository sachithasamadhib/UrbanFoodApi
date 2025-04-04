package com.urbanfood.cart.service;

import com.urbanfood.cart.dto.CartDTO;
import com.urbanfood.cart.dto.CartItemDTO;
import com.urbanfood.cart.dto.CartRequestDTO;
import com.urbanfood.cart.dto.CartResponseDTO;
import com.urbanfood.cart.exception.CartException;
import com.urbanfood.cart.model.Cart;
import com.urbanfood.cart.model.CartItem;
import com.urbanfood.cart.repository.CartItemRepository;
import com.urbanfood.cart.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartResponseDTO getCartByUserId(Long userId) {
        try {
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

            CartDTO cartDTO = convertToCartDTO(cart);
            return CartResponseDTO.success(cartDTO, "Cart retrieved successfully");
        } catch (Exception e) {
            log.error("Error retrieving cart", e);
            return CartResponseDTO.error("Something went wrong");
        }
    }

    @Override
    @Transactional
    public CartResponseDTO addItemToCart(Long userId, CartRequestDTO request) {
        try {
            // Check if cart exists
            Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
            Cart cart;

            if (cartOptional.isEmpty()) {
                // Create new cart
                cart = new Cart();
                cart.setId(cartRepository.getNextCartId());
                cart.setUserId(userId);
                cart.setCreatedDate(LocalDateTime.now());
                cart.setUpdatedDate(LocalDateTime.now());
                cart = cartRepository.save(cart);
            } else {
                cart = cartOptional.get();
            }

            // Check if item already exists in cart
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(
                    cart.getId(), request.getProductId());

            if (existingItem.isPresent()) {
                // Item already exists in cart
                return CartResponseDTO.success(
                        convertToCartDTO(cart),
                        "Item already in the cart"
                );
            }

            // Call Oracle stored procedure
            cartRepository.callAddToCartProcedure(
                    userId,
                    request.getProductId(),
                    request.getQuantity(),
                    request.getPrice()
            );

            // Return updated cart
            Cart updatedCart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartException("Cart not found after adding item"));

            return CartResponseDTO.success(
                    convertToCartDTO(updatedCart),
                    "Successful"
            );
        } catch (DataAccessException e) {
            log.error("Database error adding item to cart", e);
            return CartResponseDTO.error("Something went wrong");
        } catch (Exception e) {
            log.error("Error adding item to cart", e);
            return CartResponseDTO.error("Something went wrong");
        }
    }

    @Override
    @Transactional
    public CartResponseDTO updateCartItem(Long userId, Long productId, CartRequestDTO request) {
        try {
            // Check if cart exists
            Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
            if (cartOptional.isEmpty()) {
                return CartResponseDTO.error("Cart not found");
            }

            Cart cart = cartOptional.get();

            // Check if item exists in cart
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(
                    cart.getId(), productId);

            if (existingItem.isEmpty()) {
                return CartResponseDTO.error("Item not found in cart");
            }

            // Call Oracle stored procedure
            cartRepository.callUpdateCartQuantityProcedure(
                    userId,
                    productId,
                    request.getQuantity()
            );

            // Return updated cart
            Cart updatedCart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartException("Cart not found after updating item"));

            return CartResponseDTO.success(
                    convertToCartDTO(updatedCart),
                    "Item updated successfully"
            );
        } catch (Exception e) {
            log.error("Error updating cart item", e);
            return CartResponseDTO.error("Something went wrong");
        }
    }

    @Override
    @Transactional
    public CartResponseDTO removeItemFromCart(Long userId, Long productId) {
        try {
            // Check if cart exists
            Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
            if (cartOptional.isEmpty()) {
                return CartResponseDTO.error("Cart not found");
            }

            Cart cart = cartOptional.get();

            // Check if item exists in cart
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(
                    cart.getId(), productId);

            if (existingItem.isEmpty()) {
                return CartResponseDTO.error("Item not found in cart");
            }

            // Call Oracle stored procedure
            cartRepository.callRemoveFromCartProcedure(userId, productId);

            // Return updated cart
            Cart updatedCart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartException("Cart not found after removing item"));

            return CartResponseDTO.success(
                    convertToCartDTO(updatedCart),
                    "Item removed successfully"
            );
        } catch (Exception e) {
            log.error("Error removing item from cart", e);
            return CartResponseDTO.error("Something went wrong");
        }
    }

    @Override
    @Transactional
    public CartResponseDTO clearCart(Long userId) {
        try {
            // Check if cart exists
            Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
            if (cartOptional.isEmpty()) {
                return CartResponseDTO.error("Cart not found");
            }

            // Call Oracle stored procedure
            cartRepository.callClearCartProcedure(userId);

            // Return empty cart
            Cart emptyCart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartException("Cart not found after clearing"));

            return CartResponseDTO.success(
                    convertToCartDTO(emptyCart),
                    "Cart cleared successfully"
            );
        } catch (Exception e) {
            log.error("Error clearing cart", e);
            return CartResponseDTO.error("Something went wrong");
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