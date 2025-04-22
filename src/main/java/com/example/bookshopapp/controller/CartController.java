package com.example.bookshopapp.controller;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Cart;
import com.example.bookshopapp.service.BookService;
import com.example.bookshopapp.service.CartService;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final BookService bookService;
    private final CartService cartService;

    @Autowired
    public CartController(BookService bookService, CartService cartService) {
        this.bookService = bookService;
        this.cartService = cartService;
    }

    @GetMapping("/cart/add/{id}")
    public String addToCart(@PathVariable("id") Long id,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        Optional<Book> bookOptional = bookService.getBookById(id);
        if (!bookOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found.");
            return "redirect:/home";
        }
        Book book = bookOptional.get();
        if (userId != null) {
            cartService.addCartItem(userId, id, 1);
        }
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            if (userId != null) {
                cart = cartService.getUserCart(userId);
            }
        } else if (userId != null) {
            cartService.addCartItem(userId, id, 1);
        }
        cart.addItem(book, 1);
        session.setAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("message", "Book added to cart.");
        return "redirect:/home";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            cartService.removeCartItem(userId, id);
        }
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cart.removeItem(id);
            session.setAttribute("cart", cart);
        }
        redirectAttributes.addFlashAttribute("message", "Item removed from cart.");
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        Cart cart;
        if (userId != null) {
            cart = cartService.getUserCart(userId);
        } else {
            cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
            }
        }
        session.setAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("subtotal", cart.getSubtotal());
        return "cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Map<String, String> params,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        Cart cart = new Cart();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("quantity_")) {
                try {
                    Long bookId = Long.parseLong(entry.getKey().substring("quantity_".length()));
                    int qty = Integer.parseInt(entry.getValue());
                    if (qty > 0) {
                        Optional<Book> bookOptional = bookService.getBookById(bookId);
                        if (bookOptional.isPresent()) {
                            Book book = bookOptional.get();
                            cart.addItem(book, qty);
                            if (userId != null) {
                                cartService.updateCartItemQuantity(userId, bookId, qty);
                            }
                        }
                    } else if (userId != null) {
                        cartService.removeCartItem(userId, bookId);
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        session.setAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("message", "Cart updated.");
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            cartService.clearCart(userId);
        }
        session.removeAttribute("cart");
        redirectAttributes.addFlashAttribute("message", "Cart cleared.");
        return "redirect:/cart";
    }
}
