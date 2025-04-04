package com.urbanfood.review.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.urbanfood.review.dto.ReviewRequest;
import com.urbanfood.review.dto.ReviewResponse;
import com.urbanfood.review.dto.ReviewSummary;
import com.urbanfood.review.exception.ResourceNotFoundException;
import com.urbanfood.review.model.Review;
import com.urbanfood.review.repository.ReviewRepository;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        Review review = new Review();
        review.setUserId(reviewRequest.getUserId());
        review.setProductId(reviewRequest.getProductId());
        review.setTitle(reviewRequest.getTitle());
        review.setContent(reviewRequest.getContent());
        review.setRating(reviewRequest.getRating());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        review.setVerified(false); // Verified should be set by admin or verification process
        review.setHelpful(false);
        review.setHelpfulVotes(0);

        Review savedReview = reviewRepository.save(review);
        return convertToDto(savedReview);
    }

    @Override
    public ReviewResponse getReviewById(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + id));
        return convertToDto(review);
    }

    @Override
    public Page<ReviewResponse> getReviewsByProductId(String productId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);
        List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(reviewResponses, pageable, reviewPage.getTotalElements());
    }

    @Override
    public Page<ReviewResponse> getReviewsByUserId(String userId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByUserId(userId, pageable);
        List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(reviewResponses, pageable, reviewPage.getTotalElements());
    }

    @Override
    public ReviewResponse updateReview(String id, ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + id));

        // Only update if the user is the owner of the review
        if (!review.getUserId().equals(reviewRequest.getUserId())) {
            throw new IllegalArgumentException("User is not authorized to update this review");
        }

        review.setTitle(reviewRequest.getTitle());
        review.setContent(reviewRequest.getContent());
        review.setRating(reviewRequest.getRating());
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        return convertToDto(updatedReview);
    }

    @Override
    public void deleteReview(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + id));
        reviewRepository.delete(review);
    }

    @Override
    public ReviewResponse voteHelpful(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + id));

        review.setHelpfulVotes(review.getHelpfulVotes() + 1);
        // If helpful votes exceed a threshold, mark it as helpful
        if (review.getHelpfulVotes() >= 5) {
            review.setHelpful(true);
        }

        Review updatedReview = reviewRepository.save(review);
        return convertToDto(updatedReview);
    }

    @Override
    public ReviewSummary getReviewSummaryByProductId(String productId) {
        long totalReviews = reviewRepository.countByProductId(productId);
        long fiveStarCount = reviewRepository.countByProductIdAndRating(productId, 5);
        long fourStarCount = reviewRepository.countByProductIdAndRating(productId, 4);
        long threeStarCount = reviewRepository.countByProductIdAndRating(productId, 3);
        long twoStarCount = reviewRepository.countByProductIdAndRating(productId, 2);
        long oneStarCount = reviewRepository.countByProductIdAndRating(productId, 1);

        // Calculate average rating
        double averageRating = 0.0;
        if (totalReviews > 0) {
            averageRating = (5 * fiveStarCount + 4 * fourStarCount + 3 * threeStarCount +
                    2 * twoStarCount + oneStarCount) / (double) totalReviews;
        }

        ReviewSummary summary = new ReviewSummary();
        summary.setProductId(productId);
        summary.setAverageRating(Math.round(averageRating * 10) / 10.0); // Round to 1 decimal place
        summary.setTotalReviews((int) totalReviews);
        summary.setFiveStarCount((int) fiveStarCount);
        summary.setFourStarCount((int) fourStarCount);
        summary.setThreeStarCount((int) threeStarCount);
        summary.setTwoStarCount((int) twoStarCount);
        summary.setOneStarCount((int) oneStarCount);

        return summary;
    }

    @Override
    public List<ReviewResponse> getMostHelpfulReviews(String productId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Review> helpfulReviews = reviewRepository.findMostHelpfulReviews(productId, pageable);
        return helpfulReviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReviewResponse> getVerifiedReviewsByProductId(String productId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findVerifiedReviewsByProductId(productId, pageable);
        List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(reviewResponses, pageable, reviewPage.getTotalElements());
    }

    // Helper method to convert entity to DTO
    private ReviewResponse convertToDto(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUserId());
        response.setProductId(review.getProductId());
        response.setTitle(review.getTitle());
        response.setContent(review.getContent());
        response.setRating(review.getRating());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        response.setVerified(review.isVerified());
        response.setHelpfulVotes(review.getHelpfulVotes());
        return response;
    }
}

