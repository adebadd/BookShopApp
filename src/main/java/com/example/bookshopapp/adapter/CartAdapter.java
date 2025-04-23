package com.example.bookshopapp.adapter;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Cart;
import com.example.bookshopapp.model.CartItem;

import java.util.List;

public interface CartAdapter {
    void addItem(Long bookId, Book book, int quantity);
    
    void updateItemQuantity(Long bookId, int quantity);
    
    void removeItem(Long bookId);
    
    void clear();
    
    List<CartItem> getItems();
    
    Cart toCart();
}
