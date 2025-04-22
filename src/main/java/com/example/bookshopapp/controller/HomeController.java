package com.example.bookshopapp.controller;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final BookService bookService;
    private static final int PAGE_SIZE = 6;

    @Autowired
    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/home")
    public String home(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "title", required = false) String title,
                       @RequestParam(name = "author", required = false) String author,
                       @RequestParam(name = "category", required = false) String category,
                       @RequestParam(name = "publisher", required = false) String publisher,
                       @RequestParam(name = "sort", required = false) String sort,
                       Model model) {
        int pageNumber = page - 1;
        if (pageNumber < 0) pageNumber = 0;

        Sort sortOrder = Sort.by("title").ascending();
        if ("title".equals(sort)) {
            sortOrder = Sort.by("title").ascending();
        } else if ("author".equals(sort)) {
            sortOrder = Sort.by("authors.name").ascending();
        } else if ("priceAsc".equals(sort)) {
            sortOrder = Sort.by("price").ascending();
        } else if ("priceDesc".equals(sort)) {
            sortOrder = Sort.by("price").descending();
        }

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, sortOrder);
        Page<Book> bookPage;

        if ((title != null && !title.isEmpty()) ||
            (author != null && !author.isEmpty()) ||
            (category != null && !category.isEmpty()) ||
            (publisher != null && !publisher.isEmpty())) {
            bookPage = bookService.searchBooks(title, author, category, publisher, pageable);
        } else {
            bookPage = bookService.getAllBooksWithPagination(pageable);
        }

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("totalItems", bookPage.getTotalElements());
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("category", category);
        model.addAttribute("publisher", publisher);
        model.addAttribute("sort", sort);

        return "home";
    }
}