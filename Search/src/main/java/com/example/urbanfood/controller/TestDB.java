package com.example.urbanfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestDB {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test-db")
    public String testConnection() {
        try {
            String result = jdbcTemplate.queryForObject("SELECT 'Connection Successful' FROM DUAL", String.class);
            return "Database connection test: " + result;
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}

