package com.example.bookshopapp.factory;

import com.example.bookshopapp.adapter.CartAdapter;
import com.example.bookshopapp.adapter.DatabaseCartAdapter;
import com.example.bookshopapp.adapter.SessionCartAdapter;
import com.example.bookshopapp.model.Cart;
import com.example.bookshopapp.repository.BookRepository;
import com.example.bookshopapp.repository.CartItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CartAdapterFactory {
    
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    
    @Autowired
    public CartAdapterFactory(CartItemRepository cartItemRepository, BookRepository bookRepository) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }
    
    public CartAdapter createSessionCartAdapter(Cart cart) {
        return new SessionCartAdapter(cart);
    }
    
    public CartAdapter createDatabaseCartAdapter(Long userId) {
        return new DatabaseCartAdapter(userId, cartItemRepository, bookRepository);
    }
    
    public CartAdapter createAppropriateAdapter(Long userId, Cart sessionCart) {
        if (userId != null) {
            return createDatabaseCartAdapter(userId);
        } else {
            return createSessionCartAdapter(sessionCart);
        }
    }
}
