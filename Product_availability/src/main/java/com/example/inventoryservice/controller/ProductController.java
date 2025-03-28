package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.AvailabilityRequest;
import com.example.inventoryservice.dto.AvailabilityResponse;
import com.example.inventoryservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/check-availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(@RequestBody AvailabilityRequest request) {
        AvailabilityResponse response = productService.checkAvailability(
                request.getProductId(),
                request.getRequiredAmount()
        );
        return ResponseEntity.ok(response);
    }
}

