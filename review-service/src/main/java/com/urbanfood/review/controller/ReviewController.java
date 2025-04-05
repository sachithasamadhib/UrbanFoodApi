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

import com.urbanfood.review.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        ReviewResponse createdReview = reviewService.createReview(reviewRequest);
        ApiResponse<ReviewResponse> response = ApiResponse.success(
                "Review created successfully", createdReview);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable String id) {
        ReviewResponse review = reviewService.getReviewById(id);
        ApiResponse<ReviewResponse> response = ApiResponse.success(
                "Review retrieved successfully", review);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsByProductId(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ReviewResponse> reviews = reviewService.getReviewsByProductId(productId, pageable);

        ApiResponse<Page<ReviewResponse>> response = ApiResponse.success(
                "Reviews retrieved successfully", reviews);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getReviewsByUserId(userId, pageable);

        ApiResponse<Page<ReviewResponse>> response = ApiResponse.success(
                "User reviews retrieved successfully", reviews);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable String id,
            @Valid @RequestBody ReviewRequest reviewRequest) {

        ReviewResponse updatedReview = reviewService.updateReview(id, reviewRequest);
        ApiResponse<ReviewResponse> response = ApiResponse.success(
                "Review updated successfully", updatedReview);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        ApiResponse<Void> response = ApiResponse.success(
                "Review deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/helpful")
    public ResponseEntity<ApiResponse<ReviewResponse>> markReviewAsHelpful(@PathVariable String id) {
        ReviewResponse updatedReview = reviewService.voteHelpful(id);
        ApiResponse<ReviewResponse> response = ApiResponse.success(
                "Review marked as helpful", updatedReview);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<ApiResponse<ReviewSummary>> getReviewSummaryByProductId(@PathVariable String productId) {
        ReviewSummary summary = reviewService.getReviewSummaryByProductId(productId);
        ApiResponse<ReviewSummary> response = ApiResponse.success(
                "Review summary retrieved successfully", summary);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/helpful")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMostHelpfulReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "5") int limit) {

        List<ReviewResponse> helpfulReviews = reviewService.getMostHelpfulReviews(productId, limit);
        ApiResponse<List<ReviewResponse>> response = ApiResponse.success(
                "Most helpful reviews retrieved successfully", helpfulReviews);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/verified")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getVerifiedReviewsByProductId(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> verifiedReviews = reviewService.getVerifiedReviewsByProductId(productId, pageable);

        ApiResponse<Page<ReviewResponse>> response = ApiResponse.success(
                "Verified reviews retrieved successfully", verifiedReviews);
        return ResponseEntity.ok(response);
    }
}

