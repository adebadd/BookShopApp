package com.example.bookshopapp.repository;

import com.example.bookshopapp.model.entity.CartItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    CartItem findByUserIdAndBookId(Long userId, Long bookId);
    
    List<CartItem> findByUserId(Long userId);
    
    void deleteByUserIdAndBookId(Long userId, Long bookId);
    
    void deleteByUserId(Long userId);
}
