package com.example.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class AvailabilityResponse {
    private String status;
    private Integer availableQuantity;

    public AvailabilityResponse(String status, Integer availableQuantity) {
        this.status = status;
        this.availableQuantity = availableQuantity;
    }
}