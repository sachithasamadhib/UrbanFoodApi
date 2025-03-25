package com.sachi.Wishlist.controller;

import com.sachi.Wishlist.model.ApiResponse;
import com.sachi.Wishlist.model.wishlist;
import com.sachi.Wishlist.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<wishlist>>> getAllWishlist() {
        List<wishlist> wishlists = service.getAllWishlist();
        return ResponseEntity.ok(ApiResponse.success("Successful", wishlists));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<wishlist>> getWishlistById(@PathVariable String id) {
        Optional<wishlist> wishlist = service.getWishlistById(id);
        return wishlist.map(w -> ResponseEntity.ok(ApiResponse.success("Successful", w)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Wishlist not found with id: " + id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<wishlist>>> getWishlistByUserId(@PathVariable String userId) {
        List<wishlist> wishlists = service.getWishlistByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Successful", wishlists));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<wishlist>> createWishlist(@RequestBody wishlist wishlist) {
        wishlist savedWishlist = service.saveWishlist(wishlist);
        return new ResponseEntity<>(
                ApiResponse.success("Successful", savedWishlist),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<wishlist>> updateWishlist(@PathVariable String id, @RequestBody wishlist wishlist) {
        Optional<wishlist> existingWishlist = service.getWishlistById(id);
        if (existingWishlist.isPresent()) {
            wishlist.setId(id);
            wishlist savedWishlist = service.saveWishlist(wishlist);
            return ResponseEntity.ok(ApiResponse.success("Updated successfully", savedWishlist));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No item found with id: " + id));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteWishlist(@PathVariable String id) {
        Optional<wishlist> existingWishlist = service.getWishlistById(id);
        if (existingWishlist.isPresent()) {
            service.deleteWishlist(id);
            return ResponseEntity.ok(ApiResponse.success("Successfully Deleted", "deleted"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No item found with id: " + id));
        }
    }
}

