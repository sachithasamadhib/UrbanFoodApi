package com.example.urbanfood.controller;

import com.example.urbanfood.model.Search;
import com.example.urbanfood.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search/{searchText}")
    public List<Search> searchProductsByPath(@PathVariable("searchText") String searchText) {
        return searchService.searchProducts(searchText);
    }
}