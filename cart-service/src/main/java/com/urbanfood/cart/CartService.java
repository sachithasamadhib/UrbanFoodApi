package com.urbanfood.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CartService {
    public static void main(String[] args) {
        SpringApplication.run(CartService.class, args);
    }
}