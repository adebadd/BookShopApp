package com.example.bookshopapp.repository;

import com.example.bookshopapp.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookId(Long bookId);
    List<Review> findByUserId(Long userId);
    boolean existsByBookIdAndUserId(Long bookId, Long userId);
}
