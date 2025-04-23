package com.example.bookshopapp.service.impl;

import com.example.bookshopapp.model.Order;
import com.example.bookshopapp.repository.OrderRepository;
import com.example.bookshopapp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Long id, String status) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    @Override
    public long countOrders() {
        return orderRepository.count();
    }

    @Override
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findAll(
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "orderDate")))
            .getContent();
    }

    @Override
    public Page<Order> searchOrders(Long orderId, String customer, String status, 
                                   LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        List<Order> allOrders = orderRepository.findAll();
        List<Order> filteredOrders = new ArrayList<>();
        
        for (Order order : allOrders) {
            boolean matches = true;

            if (orderId != null && !order.getId().equals(orderId)) {
                matches = false;
            }

            if (matches && customer != null && !customer.isEmpty()) {
                boolean customerMatch = false;
                
                if (order.getUserId() != null) {
                    customerMatch = customer.toLowerCase().contains(order.getUserId().toString());
                }
                
                if (!customerMatch) {
                    matches = false;
                }
            }
            
            if (matches && status != null && !status.isEmpty()) {
                if (order.getStatus() == null || !order.getStatus().equalsIgnoreCase(status)) {
                    matches = false;
                }
            }
            
            if (matches && fromDate != null) {
                LocalDateTime fromDateTime = fromDate.atStartOfDay();
                if (order.getOrderDate().isBefore(fromDateTime)) {
                    matches = false;
                }
            }
            
            if (matches && toDate != null) {
                LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
                if (order.getOrderDate().isAfter(toDateTime)) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredOrders.add(order);
            }
        }
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredOrders.size());
        List<Order> pageContent = start >= filteredOrders.size() ? 
                new ArrayList<>() : filteredOrders.subList(start, end);
                
        return new PageImpl<>(pageContent, pageable, filteredOrders.size());
    }
}
