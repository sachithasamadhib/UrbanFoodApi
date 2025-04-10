package com.urbanfood.cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @Column(name = "PRODUCTID")
    private Long productId;

    @Column(name = "PRODUCTNAME")
    private String productName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private String status = "AVAILABLE";

    @Column(name = "AMOUNT")
    private Integer amount = 0;

    @Column(name = "BRAND")
    private String brand = "NO BRAND";

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "TAGS")
    private String tags;

    @Column(name = "MAINIMAGENAME")
    private String mainImageName;

    @Column(name = "SHIPPINGCOST") // Note: No underscore
    private Double shippingCost;

    @Column(name = "CATID")
    private Long categoryId;

    @Column(name = "SUPPLIERID")
    private Long supplierId;
}