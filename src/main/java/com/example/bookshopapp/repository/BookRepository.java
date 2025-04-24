package com.example.bookshopapp.repository;

import com.example.bookshopapp.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    long countByStockQuantityLessThan(int threshold);
    Page<Book> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Book> findByStockQuantityLessThanOrderByStockQuantityAsc(int threshold, Pageable pageable);
}
