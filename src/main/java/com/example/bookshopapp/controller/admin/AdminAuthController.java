package com.example.bookshopapp.controller.admin;

import com.example.bookshopapp.model.User;
import com.example.bookshopapp.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private final UserService userService;

    @Autowired
    public AdminAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        
        Optional<User> userOptional = userService.authenticate(email, password);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (!"ADMIN".equals(user.getRole())) {
                redirectAttributes.addFlashAttribute("error", "You do not have admin privileges.");
                return "redirect:/admin/login";
            }
            
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
            session.setAttribute("userRole", user.getRole());
            
            return "redirect:/admin/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "admin/register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String firstName,
                                     @RequestParam String lastName,
                                     @RequestParam String email,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword,
                                     RedirectAttributes redirectAttributes) {
        
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/admin/register";
        }
        
        if (userService.getUserByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already exists.");
            return "redirect:/admin/register";
        }
        
        User newAdmin = new User();
        newAdmin.setFirstName(firstName);
        newAdmin.setLastName(lastName);
        newAdmin.setEmail(email);
        newAdmin.setRole("ADMIN");
        
        boolean success = userService.register(newAdmin, password);
        
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Admin account created successfully. You can now log in.");
            return "redirect:/admin/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to register admin account.");
            return "redirect:/admin/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
