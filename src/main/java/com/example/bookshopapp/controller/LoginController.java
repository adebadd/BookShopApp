package com.example.bookshopapp.controller;

import com.example.bookshopapp.model.User;
import com.example.bookshopapp.model.Cart;
import com.example.bookshopapp.service.UserService;
import com.example.bookshopapp.service.CartService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(name = "error", required = false) Boolean error, 
            @RequestParam(name = "registered", required = false) Boolean registered,
            ModelMap model) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("errorMessage", "Invalid email or password");
        }
        if (Boolean.TRUE.equals(registered)) {
            model.addAttribute("successMessage", "Registration successful. Please login.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.authenticate(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("user", user);
            Cart sessionCart = (Cart) session.getAttribute("cart");
            if (sessionCart != null && !sessionCart.getItems().isEmpty()) {
                cartService.mergeWithSessionCart(user.getId(), sessionCart);
                Cart mergedCart = cartService.getUserCart(user.getId());
                session.setAttribute("cart", mergedCart);
            }
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin";
            }
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null) {
                session.removeAttribute("redirectAfterLogin");
                return "redirect:" + redirectUrl;
            }
            return "redirect:/home";
        }
        return "redirect:/login?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been successfully logged out.");
        return "redirect:/login";
    }
}