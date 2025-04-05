package com.example.inventoryservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @Column(name = "ProductID ")
    private Long id;

    @Column(nullable = false)
    private String ProductName ;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;


    private String brand;

    @Column(name = "Amount")
    private Integer stockQuantity;

    private String status;

}