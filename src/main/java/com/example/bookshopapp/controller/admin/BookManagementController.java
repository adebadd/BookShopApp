package com.example.bookshopapp.controller.admin;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.User;
import com.example.bookshopapp.repository.AuthorRepository;
import com.example.bookshopapp.repository.CategoryRepository;
import com.example.bookshopapp.repository.PublisherRepository;
import com.example.bookshopapp.service.BookService;
import com.example.bookshopapp.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Controller
@RequestMapping("/admin/books")
public class BookManagementController {

    private final BookService bookService;
    private final UserService userService;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;

    @Autowired
    public BookManagementController(BookService bookService, UserService userService,
                                   AuthorRepository authorRepository,
                                   CategoryRepository categoryRepository,
                                   PublisherRepository publisherRepository) {
        this.bookService = bookService;
        this.userService = userService;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
    }

    @GetMapping("")
    public String listBooks(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "title") String sortBy,
                           @RequestParam(defaultValue = "asc") String sortDir,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String author,
                           @RequestParam(required = false) String category,
                           @RequestParam(required = false) String publisher,
                           Model model,
                           HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/admin/login";
        }
        
        User currentUser = userService.getUserById(userId).orElse(null);
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/home";
        }
        
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Book> books = bookService.searchBooks(title, author, category, publisher, pageable);
        
        model.addAttribute("books", books.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("totalItems", books.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("category", category);
        model.addAttribute("publisher", publisher);
        
        model.addAttribute("publishers", publisherRepository.findAll());
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        
        return "admin/book-management";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !isAdmin(userId)) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("book", new Book());
        model.addAttribute("publishers", publisherRepository.findAll());
        model.addAttribute("authors", authorRepository.findAll());  
        model.addAttribute("categories", categoryRepository.findAll());
        
        return "admin/book-form";
    }

    @PostMapping("/create")
    public String createBook(@ModelAttribute Book book, 
                            @RequestParam(value = "authorIds", required = false) Long[] authorIds,
                            @RequestParam(value = "categoryIds", required = false) Long[] categoryIds,
                            @RequestParam(value = "publisherId", required = false) Long publisherId,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !isAdmin(userId)) {
            return "redirect:/admin/login";
        }
        
        try {
            bookService.saveBookWithRelationships(book, authorIds, categoryIds, publisherId, imageFile);
            redirectAttributes.addFlashAttribute("message", "Book created successfully.");
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating book: " + e.getMessage());
            return "redirect:/admin/books/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !isAdmin(userId)) {
            return "redirect:/admin/login";
        }
        
        Optional<Book> bookOpt = bookService.getBookById(id);
        if (!bookOpt.isPresent()) {
            return "redirect:/admin/books";
        }
        
        model.addAttribute("book", bookOpt.get());
        model.addAttribute("publishers", publisherRepository.findAll());
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        
        return "admin/book-form";
    }

    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id,
                            @ModelAttribute Book book,
                            @RequestParam(value = "authorIds", required = false) Long[] authorIds,
                            @RequestParam(value = "categoryIds", required = false) Long[] categoryIds,
                            @RequestParam(value = "publisherId", required = false) Long publisherId,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !isAdmin(userId)) {
            return "redirect:/admin/login";
        }
        
        try {
            book.setId(id);
            bookService.updateBookWithRelationships(book, authorIds, categoryIds, publisherId, imageFile);
            redirectAttributes.addFlashAttribute("message", "Book updated successfully.");
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating book: " + e.getMessage());
            return "redirect:/admin/books/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !isAdmin(userId)) {
            return "redirect:/admin/login";
        }
        
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("message", "Book deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting book: " + e.getMessage());
        }
        
        return "redirect:/admin/books";
    }
    
    @PostMapping("/updateStock/{id}")
    public String updateStock(@PathVariable Long id, 
                             @RequestParam Integer quantity,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !isAdmin(userId)) {
            return "redirect:/admin/login";
        }
        
        try {
            bookService.updateStock(id, quantity);
            redirectAttributes.addFlashAttribute("message", "Stock updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating stock: " + e.getMessage());
        }
        
        return "redirect:/admin/books";
    }
    
    private boolean isAdmin(Long userId) {
        User user = userService.getUserById(userId).orElse(null);
        return user != null && "ADMIN".equals(user.getRole());
    }
}
