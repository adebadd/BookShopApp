package com.example.bookshopapp.service;

import com.example.bookshopapp.model.Author;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<Author> getAllAuthors();
    Optional<Author> getAuthorById(Long id);
    Author saveAuthor(Author author);
    void deleteAuthor(Long id);
}
