package com.example.bookshopapp.controller;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Category;
import com.example.bookshopapp.service.BookService;
import com.example.bookshopapp.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @Autowired
    public HomeController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @GetMapping("/home")
    public String home(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String publisher,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("title"));

        Page<Book> bookPage;
        if (title != null || author != null || category != null || publisher != null) {
            bookPage = bookService.searchBooks(title, author, category, publisher, pageable);
        } else {
            bookPage = bookService.getAllBooks(pageable);
        }

        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("categoryFilter", category);
        model.addAttribute("publisher", publisher);
        model.addAttribute("categories", categories);

        return "home";
    }
}