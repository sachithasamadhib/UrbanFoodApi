package com.urbanfood.cart.Controller;

import com.urbanfood.cart.dto.*;
import com.urbanfood.cart.service.CartService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO<CartDTO>> getCart(
            @PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<ResponseDTO<CartDTO>> addToCart(
            @PathVariable @NotNull Long userId,
            @RequestParam @NotNull Long productId,
            @RequestParam @Min(1) Integer quantity) {
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<ResponseDTO<CartDTO>> updateCartItem(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long productId,
            @RequestParam @Min(1) Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<ResponseDTO<CartDTO>> removeFromCart(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long productId) {
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDTO<CartDTO>> clearCart(
            @PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }
}