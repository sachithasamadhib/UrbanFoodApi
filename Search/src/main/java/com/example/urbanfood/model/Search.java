package com.example.urbanfood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "PRODUCTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Search {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_gen")
    @SequenceGenerator(name = "product_seq_gen", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    @Column(name = "PRODUCTID")
    private Long productId;
    
    @Column(name = "NAME", nullable = false, length = 100)
    private String name;
    
    @Column(name = "WEIGHT", precision = 10, scale = 2)
    private BigDecimal weight;
    
    @Column(name = "STATUS", length = 20)
    private String status;
    
    @Column(name = "AMOUNT")
    private Integer amount;
    
    @Column(name = "DESCRIPTION", length = 4000)
    private String description;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "EXPIRYDATE")
    private Date expiryDate;
    
    @Column(name = "BRAND", length = 100)
    private String brand;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "MADEDATE")
    private Date madeDate;
    
    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "TAGS", length = 500)
    private String tags;
    
    @Column(name = "MAINIMAGENAME", length = 255)
    private String mainImageName;
    
    @Column(name = "SHIPPINGCOST", precision = 10, scale = 2)
    private BigDecimal shippingCost;

}

