package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Basic CRUD operations are provided by JpaRepository
}