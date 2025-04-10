package com.urbanfood.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CartService {
    public static void main(String[] args) {
        SpringApplication.run(CartService.class, args);
    }
}