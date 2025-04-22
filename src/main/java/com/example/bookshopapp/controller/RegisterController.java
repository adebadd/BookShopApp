package com.example.bookshopapp.controller;

import com.example.bookshopapp.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private final RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phone,
            ModelMap model) {
        
        try {
            registerService.registerUser(email, password, firstName, lastName, phone);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.put("errorMessage", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.put("errorMessage", "An unexpected error occurred. Please try again.");
            return "register";
        }
    }
}