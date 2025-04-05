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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;

    @Autowired
    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            JdbcTemplate jdbcTemplate,
            RestTemplate restTemplate) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.restTemplate = restTemplate;
    }

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

            // Send email notification
            sendEmailNotification(userId, productId);

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

    private void sendEmailNotification(Long userId, Long productId) {
        try {
            // Get user details
            Map<String, Object> userDetails = jdbcTemplate.queryForMap(
                    "SELECT email, firstname, lastname FROM user_accounts WHERE userid = ?",
                    userId
            );

            // Create notification object
            Map<String, Object> notification = new HashMap<>();
            notification.put("user_id", userId);
            notification.put("email", userDetails.get("email"));
            notification.put("firstname", userDetails.get("firstname"));
            notification.put("lastname", userDetails.get("lastname"));
            notification.put("subject", "Item removed from your cart");
            notification.put("notification_type", "CART_ITEM_REMOVED");
            notification.put("product_id", productId);
            notification.put("processed", 0);
            notification.put("created_date", LocalDateTime.now().toString());

            // Call email service
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://email-service/api/email/send-notification",
                    notification,
                    String.class
            );

            log.info("Email notification sent: {}", response.getBody());
        } catch (Exception e) {
            log.error("Failed to send email notification", e);
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

        dto.setProductName("Product " + cartItem.getProductId());

        return dto;
    }
}