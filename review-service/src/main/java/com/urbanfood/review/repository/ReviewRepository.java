package com.urbanfood.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.urbanfood.review.model.Review;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    Page<Review> findByProductId(String productId, Pageable pageable);

    Page<Review> findByUserId(String userId, Pageable pageable);

    @Query(value = "{ 'productId': ?0, 'rating': ?1 }")
    Page<Review> findByProductIdAndRating(String productId, int rating, Pageable pageable);

    @Query(value = "{ 'productId': ?0 }", count = true)
    long countByProductId(String productId);

    @Query(value = "{ 'productId': ?0, 'rating': ?1 }", count = true)
    long countByProductIdAndRating(String productId, int rating);

    @Query(value = "{ 'productId': ?0 }", sort = "{ 'helpfulVotes': -1 }")
    List<Review> findMostHelpfulReviews(String productId, Pageable pageable);

    @Query(value = "{ 'productId': ?0, 'verified': true }")
    Page<Review> findVerifiedReviewsByProductId(String productId, Pageable pageable);
}

