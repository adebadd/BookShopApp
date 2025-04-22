package com.example.bookshopapp.controller;

import com.example.bookshopapp.model.Order;
import com.example.bookshopapp.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/status")
    public String showOrderStatus(Model model, HttpSession session) {
        Order order = (Order) session.getAttribute("lastOrder");

        if (order != null) {
            model.addAttribute("order", order);
            model.addAttribute("orderItems", order.getOrderItems());
            model.addAttribute("orderNumber", generateOrderNumber(order.getId()));
        } else {
            model.addAttribute("orderNumber", generateOrderNumber(1L));
        }

        if (!model.containsAttribute("fullName")) {
            model.addAttribute("fullName", "John Doe");
            model.addAttribute("email", "john@example.com");
            model.addAttribute("phone", "+353 86 123 4567");
            model.addAttribute("address", "123 Main Street");
            model.addAttribute("city", "Dublin");
            model.addAttribute("postcode", "D01 AB12");
            model.addAttribute("country", "Ireland");
        }

        return "order-confirmation";
    }

    @GetMapping("/details/{id}")
    public String viewOrderDetails(@PathVariable("id") Long orderId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(orderId).orElse(null);

        if (order == null || !order.getUserId().equals(userId)) {
            return "redirect:/account";
        }

        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        model.addAttribute("orderNumber", generateOrderNumber(order.getId()));

        return "order-details";
    }

    private String generateOrderNumber(Long orderId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = now.format(formatter);

        return String.format("ORD-%s-%03d", datePart, orderId);
    }
}