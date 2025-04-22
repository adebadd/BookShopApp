package com.example.bookshopapp.service;

import com.example.bookshopapp.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Review> getAllReviews();
    Optional<Review> getReviewById(Long id);
    Review saveReview(Review review);
    void deleteReview(Long id);
    
    List<Review> getReviewsByBookId(Long bookId);
    List<Review> getReviewsByUserId(Long userId);
    boolean hasUserReviewedBook(Long bookId, Long userId);
}
