package com.example.bookshopapp.facade;

import com.example.bookshopapp.model.Order;

import java.util.List;
import java.util.Map;

public interface AdminDashboardFacade {
    long getTotalBooks();
    long getTotalUsers();
    long getTotalOrders();
    List<Order> getRecentOrders(int limit);
    Map<String, Object> getDashboardSummary();
}
