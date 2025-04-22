package com.example.bookshopapp.service.impl;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Cart;
import com.example.bookshopapp.model.CartItem;
import com.example.bookshopapp.repository.BookRepository;
import com.example.bookshopapp.repository.CartItemRepository;
import com.example.bookshopapp.service.BookService;
import com.example.bookshopapp.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Override
    @Transactional
    public void addCartItem(Long userId, Long bookId, Integer quantity) {
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
    public void updateCartItemQuantity(Long userId, Long bookId, Integer quantity) {
        com.example.bookshopapp.model.entity.CartItem item = cartItemRepository.findByUserIdAndBookId(userId, bookId);
        if (item != null) {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long bookId) {
        cartItemRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    @Override
    public List<CartItem> getCartItemsByUserId(Long userId) {
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
    @Transactional
    public void mergeWithSessionCart(Long userId, Cart sessionCart) {
        if (sessionCart != null && !sessionCart.getItems().isEmpty()) {
            for (CartItem item : sessionCart.getItems()) {
                addCartItem(userId, item.getBook().getId(), item.getQuantity());
            }
        }
    }

    @Override
    public Cart getUserCart(Long userId) {
        List<CartItem> items = getCartItemsByUserId(userId);
        Cart cart = new Cart();
        items.forEach(item -> cart.addItem(item.getBook(), item.getQuantity()));
        return cart;
    }
}
