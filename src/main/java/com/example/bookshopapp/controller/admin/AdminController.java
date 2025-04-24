package com.example.bookshopapp.controller.admin;

import com.example.bookshopapp.facade.AdminDashboardFacade;
import com.example.bookshopapp.model.User;
import com.example.bookshopapp.service.BookService;
import com.example.bookshopapp.service.OrderService;
import com.example.bookshopapp.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookService bookService;
    private final OrderService orderService;
    private final UserService userService;
    private final AdminDashboardFacade dashboardFacade;

    @Autowired
    public AdminController(
        BookService bookService, 
        OrderService orderService, 
        UserService userService, 
        AdminDashboardFacade dashboardFacade
    ) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.userService = userService;
        this.dashboardFacade = dashboardFacade;
    }

    @GetMapping("")
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/admin/login";
        }
        
        User currentUser = userService.getUserById(userId).orElse(null);
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/home";
        }
        
        try {
            Map<String, Object> dashboardData = dashboardFacade.getDashboardSummary();
            model.addAllAttributes(dashboardData);
            model.addAttribute("adminName", currentUser.getFirstName());
        } catch (Exception e) {
            model.addAttribute("totalBooks", 0);
            model.addAttribute("totalUsers", 0);
            model.addAttribute("totalOrders", 0);
            model.addAttribute("recentOrders", java.util.Collections.emptyList());
            model.addAttribute("error", "Failed to load dashboard data");
        }
        
        return "admin/admin-dashboard";
    }
}
