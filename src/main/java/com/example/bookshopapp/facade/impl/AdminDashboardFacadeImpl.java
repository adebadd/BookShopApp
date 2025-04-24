package com.example.bookshopapp.facade.impl;

import com.example.bookshopapp.facade.AdminDashboardFacade;
import com.example.bookshopapp.model.Order;
import com.example.bookshopapp.service.BookService;
import com.example.bookshopapp.service.OrderService;
import com.example.bookshopapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AdminDashboardFacadeImpl implements AdminDashboardFacade {

    private final BookService bookService;
    private final OrderService orderService;
    private final UserService userService;
    
    @Autowired
    public AdminDashboardFacadeImpl(
        BookService bookService, 
        OrderService orderService, 
        UserService userService
    ) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.userService = userService;
    }
    
    @Override
    public long getTotalBooks() {
        try {
            return bookService.countBooks();
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public long getTotalUsers() {
        try {
            return userService.countUsers();
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public long getTotalOrders() {
        try {
            return orderService.countOrders();
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public List<Order> getRecentOrders(int limit) {
        try {
            return orderService.getRecentOrders(limit);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    @Override
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalBooks", getTotalBooks());
        summary.put("totalUsers", getTotalUsers());
        summary.put("totalOrders", getTotalOrders());
        summary.put("recentOrders", getRecentOrders(5));
        
        return summary;
    }
}
