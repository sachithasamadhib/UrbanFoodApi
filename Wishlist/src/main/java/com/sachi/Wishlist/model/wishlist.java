package com.sachi.Wishlist.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "wishlist")
public class wishlist {
    @Id
    private String id;
    private String userId;
    private String productId;
    private String productName;


    public wishlist() {
    }

    public wishlist(String userId, String productId, String productName) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}

