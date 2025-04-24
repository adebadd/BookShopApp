package com.example.bookshopapp.controller;

import com.example.bookshopapp.model.User;
import com.example.bookshopapp.model.Order;
import com.example.bookshopapp.service.UserService;
import com.example.bookshopapp.service.OrderService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;

    @GetMapping("/users")
    public String redirectToCustomers() {
        return "redirect:/admin/customers";
    }

    @GetMapping("/customers")
    public String listCustomers(@RequestParam(name = "page", defaultValue = "1") int page,
                             @RequestParam(name = "size", defaultValue = "10") int size,
                             @RequestParam(name = "sort", defaultValue = "id") String sort,
                             @RequestParam(name = "direction", defaultValue = "asc") String direction,
                             Model model,
                             HttpSession session) {
        
        if (session.getAttribute("userRole") == null || 
            !session.getAttribute("userRole").toString().equals("ADMIN")) {
            return "redirect:/login";
        }
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortDirection, sort));
        Page<User> usersPage = userService.getAllUsers(pageable);
        
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalItems", usersPage.getTotalElements());
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("size", size);
        
        return "customer-management";
    }
    
    @GetMapping("/customers/{id}")
    public String viewCustomerDetails(@PathVariable("id") Long userId,
                                     Model model,
                                     HttpSession session) {
                                     
        if (session.getAttribute("userRole") == null || 
            !session.getAttribute("userRole").toString().equals("ADMIN")) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (!userOpt.isPresent()) {
            return "redirect:/admin/customers";
        }
        
        User user = userOpt.get();
        List<Order> userOrders = orderService.getOrdersByUserId(userId);
        
        model.addAttribute("user", user);
        model.addAttribute("orders", userOrders);
        
        return "customer-details";
    }
    
    @PostMapping("/customers/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Long userId,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
                                  
        if (session.getAttribute("userRole") == null || 
            !session.getAttribute("userRole").toString().equals("ADMIN")) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean active = user.isActive();
            user.setActive(!active);
            userService.updateUser(user, userId);
            
            String status = user.isActive() ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("message", 
                "User " + user.getEmail() + " has been " + status);
        }
        
        return "redirect:/admin/customers";
    }
}
