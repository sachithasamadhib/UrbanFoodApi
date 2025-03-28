package com.example.inventoryservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PRODUCTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @Column(name = "PRODUCTID")
    private Long productId;
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "BRAND")
    private String brand;
    
    @Column(name = "PRICE")
    private BigDecimal price;
    
    @Column(name = "AMOUNT")
    private Integer amount;
    
    @Column(name = "EXPIRYDATE")
    private LocalDate expiryDate;
    
    @Column(name = "MADEDATE")
    private LocalDate madeDate;
    
    @Column(name = "MAINIMAGENAME")
    private String mainImageName;
    
    @Column(name = "SHIPPINGCOST")
    private BigDecimal shippingCost;
    
    @Column(name = "STATUS")
    private String status;
    
    @Column(name = "TAGS")
    private String tags;
    
    @Column(name = "WEIGHT")
    private Double weight;
}

