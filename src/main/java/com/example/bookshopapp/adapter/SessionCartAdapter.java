package com.example.bookshopapp.adapter;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Cart;
import com.example.bookshopapp.model.CartItem;

import java.util.List;

public class SessionCartAdapter implements CartAdapter {
    
    private final Cart cart;
    
    public SessionCartAdapter(Cart cart) {
        this.cart = cart != null ? cart : new Cart();
    }
    
    @Override
    public void addItem(Long bookId, Book book, int quantity) {
        cart.addItem(book, quantity);
    }
    
    @Override
    public void updateItemQuantity(Long bookId, int quantity) {
        cart.updateItemQuantity(bookId, quantity);
    }
    
    @Override
    public void removeItem(Long bookId) {
        cart.removeItem(bookId);
    }
    
    @Override
    public void clear() {
        cart.getItems().clear();
    }
    
    @Override
    public List<CartItem> getItems() {
        return cart.getItems();
    }
    
    @Override
    public Cart toCart() {
        return cart;
    }
}
