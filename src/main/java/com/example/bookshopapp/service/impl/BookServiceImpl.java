package com.example.bookshopapp.service.impl;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.repository.BookRepository;
import com.example.bookshopapp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    @Override
    public Page<Book> getAllBooksWithPagination(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> searchBooks(String title, String author, String category, String publisher) {
        List<Book> results = new ArrayList<>();
        
        if (title != null && !title.isEmpty()) {
            results.addAll(findByTitle(title));
        } else if (author != null && !author.isEmpty()) {
            results.addAll(findByAuthor(author));
        } else if (category != null && !category.isEmpty()) {
            results.addAll(findByCategory(category));
        } else if (publisher != null && !publisher.isEmpty()) {
            results.addAll(findByPublisher(publisher));
        } else {
            results = getAllBooks();
        }

        return results.stream().distinct().collect(Collectors.toList());
    }
    
    @Override
    public Page<Book> searchBooks(String title, String author, String category, String publisher, Pageable pageable) {
        Specification<Book> spec = Specification.where(null);
        
        if (title != null && !title.isEmpty()) {
            spec = spec.and((root, query, builder) -> 
                builder.like(builder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        
        if (author != null && !author.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                query.distinct(true);
                return criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("authors").get("name")), 
                    "%" + author.toLowerCase() + "%"
                );
            });
        }
        
        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                query.distinct(true);
                return criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("categories").get("name")), 
                    "%" + category.toLowerCase() + "%"
                );
            });
        }
        
        if (publisher != null && !publisher.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("publisher").get("name")), 
                    "%" + publisher.toLowerCase() + "%"
                )
            );
        }
        
        return bookRepository.findAll(spec, pageable);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthorNameContainingIgnoreCase(author);
    }

    @Override
    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategoryNameContainingIgnoreCase(category);
    }

    @Override
    public List<Book> findByPublisher(String publisher) {
        return bookRepository.findByPublisherNameContainingIgnoreCase(publisher);
    }

    @Override
    @Transactional
    public boolean updateStock(Long bookId, int quantity) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            int newStock = book.getStockQuantity() + quantity;
            if (newStock >= 0) {
                book.setStockQuantity(newStock);
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInStock(Long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        return bookOptional.map(book -> book.getStockQuantity() > 0).orElse(false);
    }
}
