package com.example.bookshopapp.controller;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Review;
import com.example.bookshopapp.service.BookService;
import com.example.bookshopapp.service.ReviewService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class BookController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @Autowired
    public BookController(BookService bookService, ReviewService reviewService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    @GetMapping("/book/{id}")
    public String showBookDetails(@PathVariable("id") Long id, Model model) {
        Optional<Book> bookOptional = bookService.getBookById(id);
        
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            model.addAttribute("book", book);
            
            List<Review> reviews = reviewService.getReviewsByBookId(id);
            model.addAttribute("reviews", reviews);
            
            return "book-details";
        } else {
            return "redirect:/home";
        }
    }
    
    @PostMapping("/book/{id}/review")
    public String submitReview(
            @PathVariable("id") Long bookId,
            @RequestParam("rating") int rating,
            @RequestParam("comment") String comment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to submit a review.");
            return "redirect:/login";
        }
        
        if (reviewService.hasUserReviewedBook(bookId, userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already reviewed this book.");
            return "redirect:/book/" + bookId;
        }
        
        Optional<Book> bookOptional = bookService.getBookById(bookId);
        if (!bookOptional.isPresent()) {
            return "redirect:/home";
        }
        
        Review review = new Review();
        review.setBook(bookOptional.get());
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment);
        
        reviewService.saveReview(review);
        
        redirectAttributes.addFlashAttribute("reviewSuccess", "Your review has been submitted successfully!");
        return "redirect:/book/" + bookId;
    }
}