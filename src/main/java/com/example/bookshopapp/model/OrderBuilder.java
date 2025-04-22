package com.example.bookshopapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderBuilder {
    private Long userId;
    private BigDecimal totalAmount;
    private String status = "PENDING";
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;
    private String paymentMethod;
    
    public OrderBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }
    
    public OrderBuilder withTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }
    
    public OrderBuilder withStatus(String status) {
        this.status = status;
        return this;
    }
    
    public OrderBuilder withShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
        return this;
    }
    
    public OrderBuilder withShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
        return this;
    }
    
    public OrderBuilder withShippingState(String shippingState) {
        this.shippingState = shippingState;
        return this;
    }
    
    public OrderBuilder withShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
        return this;
    }
    
    public OrderBuilder withShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
        return this;
    }
    
    public OrderBuilder withPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }
    
    public Order build() {
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setStatus(status);
        order.setShippingAddress(shippingAddress);
        order.setShippingCity(shippingCity);
        order.setShippingState(shippingState);
        order.setShippingPostalCode(shippingPostalCode);
        order.setShippingCountry(shippingCountry);
        order.setPaymentMethod(paymentMethod);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());
        return order;
    }
}
