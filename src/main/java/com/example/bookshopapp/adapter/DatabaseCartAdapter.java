package com.example.bookshopapp.adapter;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Cart;
import com.example.bookshopapp.model.CartItem;
import com.example.bookshopapp.repository.BookRepository;
import com.example.bookshopapp.repository.CartItemRepository;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseCartAdapter implements CartAdapter {
    
    private final Long userId;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    
    public DatabaseCartAdapter(Long userId, CartItemRepository cartItemRepository, BookRepository bookRepository) {
        this.userId = userId;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }
    
    @Override
    @Transactional
    public void addItem(Long bookId, Book book, int quantity) {
        com.example.bookshopapp.model.entity.CartItem existingItem = cartItemRepository.findByUserIdAndBookId(userId, bookId);
        
        if (existingItem != null) {
            existingItem.setQuantity(quantity);
            cartItemRepository.save(existingItem);
        } else {
            com.example.bookshopapp.model.entity.CartItem newItem = new com.example.bookshopapp.model.entity.CartItem();
            newItem.setUserId(userId);
            newItem.setBookId(bookId);
            newItem.setQuantity(quantity);
            newItem.setAddedAt(LocalDateTime.now());
            cartItemRepository.save(newItem);
        }
    }
    
    @Override
    @Transactional
    public void updateItemQuantity(Long bookId, int quantity) {
        com.example.bookshopapp.model.entity.CartItem item = cartItemRepository.findByUserIdAndBookId(userId, bookId);
        if (item != null) {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }
    
    @Override
    @Transactional
    public void removeItem(Long bookId) {
        cartItemRepository.deleteByUserIdAndBookId(userId, bookId);
    }
    
    @Override
    @Transactional
    public void clear() {
        cartItemRepository.deleteByUserId(userId);
    }
    
    @Override
    public List<CartItem> getItems() {
        List<com.example.bookshopapp.model.entity.CartItem> dbItems = cartItemRepository.findByUserId(userId);
        List<CartItem> items = new ArrayList<>();
        
        for (com.example.bookshopapp.model.entity.CartItem dbItem : dbItems) {
            Optional<Book> bookOpt = bookRepository.findById(dbItem.getBookId());
            if (bookOpt.isPresent()) {
                CartItem item = new CartItem();
                item.setBook(bookOpt.get());
                item.setQuantity(dbItem.getQuantity());
                items.add(item);
            }
        }
        
        return items;
    }
    
    @Override
    public Cart toCart() {
        Cart cart = new Cart();
        List<CartItem> items = getItems();
        
        for (CartItem item : items) {
            cart.addItem(item.getBook(), item.getQuantity());
        }
        
        return cart;
    }
}
