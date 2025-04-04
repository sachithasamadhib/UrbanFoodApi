package com.urbanfood.review.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.urbanfood.review.dto.ReviewRequest;
import com.urbanfood.review.dto.ReviewResponse;
import com.urbanfood.review.dto.ReviewSummary;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest reviewRequest);

    ReviewResponse getReviewById(String id);

    Page<ReviewResponse> getReviewsByProductId(String productId, Pageable pageable);

    Page<ReviewResponse> getReviewsByUserId(String userId, Pageable pageable);

    ReviewResponse updateReview(String id, ReviewRequest reviewRequest);

    void deleteReview(String id);

    ReviewResponse voteHelpful(String id);

    ReviewSummary getReviewSummaryByProductId(String productId);

    List<ReviewResponse> getMostHelpfulReviews(String productId, int limit);

    Page<ReviewResponse> getVerifiedReviewsByProductId(String productId, Pageable pageable);
}

