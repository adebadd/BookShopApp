package com.example.bookshopapp.controller.admin;

import com.example.bookshopapp.model.Order;
import com.example.bookshopapp.model.User;
import com.example.bookshopapp.service.OrderService;
import com.example.bookshopapp.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class OrderManagementController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderManagementController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("")
    public String listOrders(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "orderDate") String sortBy,
                            @RequestParam(defaultValue = "desc") String sortDir,
                            @RequestParam(required = false) Long orderId,
                            @RequestParam(required = false) String customer,
                            @RequestParam(required = false) String status,
                            Model model,
                            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !isAdmin(userId)) {
            return "redirect:/admin/login";
        }
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Order> orders = orderService.searchOrders(orderId, customer, status, null, null, pageable);
        
        model.addAttribute("orders", orders.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("totalItems", orders.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("orderId", orderId);
        model.addAttribute("customer", customer);
        model.addAttribute("status", status);
        model.addAttribute("size", size);
        
        return "admin/order-management";
    }

    @GetMapping("/view/{id}")
    public String viewOrder(@PathVariable Long id, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !isAdmin(userId)) {
            return "redirect:/admin/login";
        }
        
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (!orderOpt.isPresent()) {
            return "redirect:/admin/orders";
        }
        
        model.addAttribute("order", orderOpt.get());
        
        return "admin/order-details";
    }

    @PostMapping("/updateStatus/{id}")
    public String updateOrderStatus(@PathVariable Long id, 
                                  @RequestParam String newStatus,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !isAdmin(userId)) {
            return "redirect:/admin/login";
        }
        
        try {
            orderService.updateOrderStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("message", "Order status updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating order status: " + e.getMessage());
        }
        
        return "redirect:/admin/orders";
    }

    @GetMapping("/invoice/{id}")
    public String generateInvoice(@PathVariable Long id, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !isAdmin(userId)) {
            return "redirect:/admin/login";
        }
        
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (!orderOpt.isPresent()) {
            return "redirect:/admin/orders";
        }
        
        model.addAttribute("order", orderOpt.get());
        
        return "admin/order-invoice";
    }
    
    private boolean isAdmin(Long userId) {
        User user = userService.getUserById(userId).orElse(null);
        return user != null && "ADMIN".equals(user.getRole());
    }
}
