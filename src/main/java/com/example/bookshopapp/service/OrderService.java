package com.example.bookshopapp.service;

import com.example.bookshopapp.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long id);
    List<Order> getOrdersByUserId(Long userId);
    Order saveOrder(Order order);
    void deleteOrder(Long id);
    boolean updateOrderStatus(Long id, String status);
}
