package com.example.bookshopapp.service;

import com.example.bookshopapp.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> getAllBooks();
    Page<Book> getAllBooks(Pageable pageable);
    Page<Book> getAllBooksWithPagination(Pageable pageable);
    Optional<Book> getBookById(Long id);
    Book saveBook(Book book);
    void saveBookWithRelationships(Book book, Long[] authorIds, Long[] categoryIds, Long publisherId, MultipartFile imageFile);
    void updateBookWithRelationships(Book book, Long[] authorIds, Long[] categoryIds, Long publisherId, MultipartFile imageFile);
    void deleteBook(Long id);
    boolean updateStock(Long id, int quantity);
    Page<Book> searchBooks(String title, String author, String category, String publisher, Pageable pageable);
    long countBooks();
}
