package com.urbanfood.cart.controller;

import com.urbanfood.cart.dto.CartRequestDTO;
import com.urbanfood.cart.dto.CartResponseDTO;
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
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Long userId) {
        CartResponseDTO response = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponseDTO> addItemToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartRequestDTO request) {
        CartResponseDTO response = cartService.addItemToCart(userId, request);

        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody CartRequestDTO request) {
        CartResponseDTO response = cartService.updateCartItem(userId, productId, request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponseDTO> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        CartResponseDTO response = cartService.removeItemFromCart(userId, productId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> clearCart(@PathVariable Long userId) {
        CartResponseDTO response = cartService.clearCart(userId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}