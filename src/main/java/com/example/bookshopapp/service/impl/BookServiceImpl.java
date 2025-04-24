package com.example.bookshopapp.service.impl;

import com.example.bookshopapp.model.Author;
import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Category;
import com.example.bookshopapp.model.Publisher;
import com.example.bookshopapp.repository.AuthorRepository;
import com.example.bookshopapp.repository.BookRepository;
import com.example.bookshopapp.repository.CategoryRepository;
import com.example.bookshopapp.repository.PublisherRepository;
import com.example.bookshopapp.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
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
        book.setUpdatedAt(LocalDateTime.now());
        if (book.getCreatedAt() == null) {
            book.setCreatedAt(LocalDateTime.now());
        }
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void saveBookWithRelationships(Book book, Long[] authorIds, Long[] categoryIds, Long publisherId,
            MultipartFile imageFile) {
        if (publisherId != null) {
            Publisher publisher = publisherRepository.findById(publisherId)
                    .orElseThrow(() -> new RuntimeException("Publisher not found"));
            book.setPublisher(publisher);
        }

        Set<Author> authors = new HashSet<>();
        if (authorIds != null) {
            for (Long authorId : authorIds) {
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Author not found: " + authorId));
                authors.add(author);
            }
        }
        book.setAuthors(authors);

        Set<Category> categories = new HashSet<>();
        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                categories.add(category);
            }
        }
        book.setCategories(categories);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadsDir = "src/main/resources/static/images/";
                String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get(uploadsDir + uniqueFileName);
                Files.createDirectories(path.getParent());
                Files.write(path, imageFile.getBytes());
                book.setImageUrl("/images/" + uniqueFileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image file", e);
            }
        }

        book.setUpdatedAt(LocalDateTime.now());
        if (book.getCreatedAt() == null) {
            book.setCreatedAt(LocalDateTime.now());
        }

        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void updateBookWithRelationships(Book book, Long[] authorIds, Long[] categoryIds, Long publisherId,
            MultipartFile imageFile) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        existingBook.setTitle(book.getTitle());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setDescription(book.getDescription());
        existingBook.setPrice(book.getPrice());
        existingBook.setStockQuantity(book.getStockQuantity());
        existingBook.setPublicationYear(book.getPublicationYear());
        existingBook.setUpdatedAt(LocalDateTime.now());

        if (publisherId != null) {
            Publisher publisher = publisherRepository.findById(publisherId)
                    .orElseThrow(() -> new RuntimeException("Publisher not found"));
            existingBook.setPublisher(publisher);
        }

        Set<Author> authors = new HashSet<>();
        if (authorIds != null) {
            for (Long authorId : authorIds) {
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Author not found: " + authorId));
                authors.add(author);
            }
            existingBook.setAuthors(authors);
        }

        Set<Category> categories = new HashSet<>();
        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                categories.add(category);
            }
            existingBook.setCategories(categories);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadsDir = "src/main/resources/static/images/";
                String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get(uploadsDir + uniqueFileName);
                Files.createDirectories(path.getParent());
                Files.write(path, imageFile.getBytes());
                existingBook.setImageUrl("/images/" + uniqueFileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image file", e);
            }
        }

        bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public boolean updateStock(Long id, int quantity) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            int newQuantity = book.getStockQuantity() + quantity;
            if (newQuantity < 0) {
                return false;
            }
            book.setStockQuantity(newQuantity);
            book.setUpdatedAt(LocalDateTime.now());
            bookRepository.save(book);
            return true;
        }
        return false;
    }

    @Override
    public Page<Book> searchBooks(String title, String author, String category, String publisher, Pageable pageable) {
        List<Book> allBooks = bookRepository.findAll();
        List<Book> filteredBooks = new ArrayList<>();

        for (Book book : allBooks) {
            boolean matches = true;
            if (title != null && !title.isEmpty()) {
                if (book.getTitle() == null || !book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                    matches = false;
                }
            }

            if (matches && author != null && !author.isEmpty()) {
                boolean authorMatch = false;
                if (book.getAuthors() != null) {
                    for (Author bookAuthor : book.getAuthors()) {
                        if (bookAuthor.getName() != null &&
                                bookAuthor.getName().toLowerCase().contains(author.toLowerCase())) {
                            authorMatch = true;
                            break;
                        }
                    }
                }
                if (!authorMatch)
                    matches = false;
            }
            if (matches && category != null && !category.isEmpty()) {
                boolean categoryMatch = false;
                if (book.getCategories() != null) {
                    for (Category bookCategory : book.getCategories()) {
                        if (bookCategory.getName() != null &&
                                bookCategory.getName().toLowerCase().contains(category.toLowerCase())) {
                            categoryMatch = true;
                            break;
                        }
                    }
                }
                if (!categoryMatch)
                    matches = false;
            }
            if (matches && publisher != null && !publisher.isEmpty()) {
                if (book.getPublisher() == null || book.getPublisher().getName() == null ||
                        !book.getPublisher().getName().toLowerCase().contains(publisher.toLowerCase())) {
                    matches = false;
                }
            }

            if (matches) {
                filteredBooks.add(book);
            }
        }
        // test to mayb manually handle pagination??
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredBooks.size());

        List<Book> pageContent = start >= filteredBooks.size() ? new ArrayList<>() : filteredBooks.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filteredBooks.size());
    }

    @Override
    public long countBooks() {
        return bookRepository.count();
    }
}
