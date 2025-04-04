package com.urbanfood.review.controller;

import java.util.List;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.urbanfood.review.dto.ReviewRequest;
import com.urbanfood.review.dto.ReviewResponse;
import com.urbanfood.review.dto.ReviewSummary;
import com.urbanfood.review.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        ReviewResponse createdReview = reviewService.createReview(reviewRequest);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable String id) {
        ReviewResponse review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByProductId(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ReviewResponse> reviews = reviewService.getReviewsByProductId(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getReviewsByUserId(userId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable String id,
            @Valid @RequestBody ReviewRequest reviewRequest) {

        ReviewResponse updatedReview = reviewService.updateReview(id, reviewRequest);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/helpful")
    public ResponseEntity<ReviewResponse> markReviewAsHelpful(@PathVariable String id) {
        ReviewResponse updatedReview = reviewService.voteHelpful(id);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<ReviewSummary> getReviewSummaryByProductId(@PathVariable String productId) {
        ReviewSummary summary = reviewService.getReviewSummaryByProductId(productId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/product/{productId}/helpful")
    public ResponseEntity<List<ReviewResponse>> getMostHelpfulReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "5") int limit) {

        List<ReviewResponse> helpfulReviews = reviewService.getMostHelpfulReviews(productId, limit);
        return ResponseEntity.ok(helpfulReviews);
    }

    @GetMapping("/product/{productId}/verified")
    public ResponseEntity<Page<ReviewResponse>> getVerifiedReviewsByProductId(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> verifiedReviews = reviewService.getVerifiedReviewsByProductId(productId, pageable);
        return ResponseEntity.ok(verifiedReviews);
    }
}

