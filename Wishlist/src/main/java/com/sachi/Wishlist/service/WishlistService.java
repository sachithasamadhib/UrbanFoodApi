package com.sachi.Wishlist.service;

import com.sachi.Wishlist.model.wishlist;
import com.sachi.Wishlist.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository repository;

    public List<wishlist> getAllWishlist() {
        return repository.findAll();
    }

    public Optional<wishlist> getWishlistById(String id) {
        return repository.findById(id);
    }

    public List<wishlist> getWishlistByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    public wishlist saveWishlist(wishlist wishlist) {
        return repository.save(wishlist);
    }

    public String deleteWishlist(String id) {
        repository.deleteById(id);
        return id +"Deleted Successfully" ;
    }
}

