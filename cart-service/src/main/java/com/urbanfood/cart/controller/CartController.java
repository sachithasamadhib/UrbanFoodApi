package com.urbanfood.cart.controller;

import com.urbanfood.cart.dto.CartDTO;
import com.urbanfood.cart.dto.CartRequestDTO;
import com.urbanfood.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        CartDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartRequestDTO request) {
        CartDTO updatedCart = cartService.addItemToCart(userId, request);
        return new ResponseEntity<>(updatedCart, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody CartRequestDTO request) {
        CartDTO updatedCart = cartService.updateCartItem(userId, productId, request);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        CartDTO updatedCart = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}