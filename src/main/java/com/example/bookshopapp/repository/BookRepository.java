package com.example.bookshopapp.repository;

import com.example.bookshopapp.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :author, '%'))")
    List<Book> findByAuthorNameContainingIgnoreCase(@Param("author") String author);
    
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Book> findByCategoryNameContainingIgnoreCase(@Param("category") String category);
    
    @Query("SELECT b FROM Book b JOIN b.publisher p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :publisher, '%'))")
    List<Book> findByPublisherNameContainingIgnoreCase(@Param("publisher") String publisher);
}
