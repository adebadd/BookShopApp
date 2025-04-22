package com.example.bookshopapp.service;

import com.example.bookshopapp.model.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> getAllBooks();
    Page<Book> getAllBooksWithPagination(Pageable pageable);
    Optional<Book> getBookById(Long id);
    Book saveBook(Book book);
    void deleteBook(Long id);
    
    List<Book> searchBooks(String title, String author, String category, String publisher);
    Page<Book> searchBooks(String title, String author, String category, String publisher, Pageable pageable);
    
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByCategory(String category);
    List<Book> findByPublisher(String publisher);
    
    boolean updateStock(Long bookId, int quantity);
    boolean isInStock(Long bookId);
}
