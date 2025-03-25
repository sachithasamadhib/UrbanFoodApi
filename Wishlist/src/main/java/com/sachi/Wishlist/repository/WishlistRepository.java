package com.sachi.Wishlist.repository;

import com.sachi.Wishlist.model.wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends MongoRepository<wishlist, String> {
    List<wishlist> findByUserId(String userId); // Changed from findByUser_id to findByUserId
}

