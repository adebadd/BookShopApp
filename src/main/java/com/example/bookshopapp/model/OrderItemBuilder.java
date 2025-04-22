package com.example.bookshopapp.model;

import java.math.BigDecimal;

public class OrderItemBuilder {
    private Order order;
    private Book book;
    private Integer quantity;
    private BigDecimal unitPrice;
    
    public OrderItemBuilder withOrder(Order order) {
        this.order = order;
        return this;
    }
    
    public OrderItemBuilder withBook(Book book) {
        this.book = book;
        return this;
    }
    
    public OrderItemBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
    
    public OrderItemBuilder withUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }
    
    public OrderItem build() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(book);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);

        if (quantity != null && unitPrice != null) {
            orderItem.setTotalPrice(unitPrice.multiply(new BigDecimal(quantity)));
        }
        
        return orderItem;
    }
}
