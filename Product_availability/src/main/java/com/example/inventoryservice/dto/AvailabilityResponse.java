package com.example.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponse {
    private Long productId;
    private String productName;
    private Integer requiredAmount;
    private boolean available;
    private Integer availableQuantity;
}