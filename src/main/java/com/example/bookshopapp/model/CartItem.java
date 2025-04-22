package com.example.bookshopapp.model;

import java.math.BigDecimal;

public class CartItem {
    private Book book;
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(Book book, Integer quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBookPrice() {
        return book != null ? book.getPrice() : BigDecimal.ZERO;
    }

    public BigDecimal getTotal() {
        return book != null ? book.getPrice().multiply(new BigDecimal(quantity)) : BigDecimal.ZERO;
    }
}
