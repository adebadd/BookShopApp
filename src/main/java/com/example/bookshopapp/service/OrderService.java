package com.example.bookshopapp.service;

import com.example.bookshopapp.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();
    
    Page<Order> getAllOrders(Pageable pageable);
    
    Optional<Order> getOrderById(Long id);
    
    List<Order> getOrdersByUserId(Long userId);
    
    Order saveOrder(Order order);
    
    void deleteOrder(Long id);
    
    boolean updateOrderStatus(Long id, String status);
    
    Page<Order> searchOrders(Long orderId, String customer, String status, 
                             LocalDate fromDate, LocalDate toDate, Pageable pageable);
                             
    long countOrders();
    
    List<Order> getRecentOrders(int limit);

    BigDecimal calculateTotalRevenue();

    Map<String, Object> getMonthlyOrderStats();
}
