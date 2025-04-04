package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.AvailabilityRequest;
import com.example.inventoryservice.dto.AvailabilityResponse;
import com.example.inventoryservice.model.Product;
import com.example.inventoryservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OraclePLSQLService oraclePLSQLService;

    @Autowired
    public ProductService(ProductRepository productRepository, OraclePLSQLService oraclePLSQLService) {
        this.productRepository = productRepository;
        this.oraclePLSQLService = oraclePLSQLService;
    }

    public AvailabilityResponse checkAvailability(AvailabilityRequest request) {
        // Call the Oracle stored procedure
        Map<String, Object> result = oraclePLSQLService.checkProductAvailability(
                request.getProductId(),
                request.getRequiredAmount()
        );

        // Get the product details
        Optional<Product> productOpt = productRepository.findById(request.getProductId());

        // Extract values from the stored procedure result
        Number isAvailableNum = (Number) result.get("p_is_available");
        Number availableQuantityNum = (Number) result.get("p_available_quantity");

        boolean isAvailable = isAvailableNum != null && isAvailableNum.intValue() == 1;
        int availableQuantity = availableQuantityNum != null ? availableQuantityNum.intValue() : 0;

        // Build the response
        AvailabilityResponse.AvailabilityResponseBuilder responseBuilder = AvailabilityResponse.builder()
                .productId(request.getProductId())
                .requiredAmount(request.getRequiredAmount())
                .available(isAvailable)
                .availableQuantity(availableQuantity);

        // Add product name if product exists
        productOpt.ifPresent(product -> responseBuilder.productName(product.getName()));

        return responseBuilder.build();
    }
}