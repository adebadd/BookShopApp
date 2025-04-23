package com.example.bookshopapp.service.impl;

import com.example.bookshopapp.factory.CartAdapterFactory;
import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Cart;
import com.example.bookshopapp.model.CartItem;
import com.example.bookshopapp.repository.BookRepository;
import com.example.bookshopapp.repository.CartItemRepository;
import com.example.bookshopapp.service.BookService;
import com.example.bookshopapp.service.CartService;
import com.example.bookshopapp.adapter.CartAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final CartAdapterFactory cartAdapterFactory;

    @Autowired
    public CartServiceImpl(CartItemRepository cartItemRepository, BookRepository bookRepository, 
                          BookService bookService, CartAdapterFactory cartAdapterFactory) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.cartAdapterFactory = cartAdapterFactory;
    }

    @Override
    @Transactional
    public void addCartItem(Long userId, Long bookId, Integer quantity) {
        CartAdapter adapter = cartAdapterFactory.createDatabaseCartAdapter(userId);
        Book book = bookService.getBookById(bookId).orElseThrow(() -> 
            new IllegalArgumentException("Book not found with ID: " + bookId));
        adapter.addItem(bookId, book, quantity);
    }

    @Override
    @Transactional
    public void updateCartItemQuantity(Long userId, Long bookId, Integer quantity) {
        CartAdapter adapter = cartAdapterFactory.createDatabaseCartAdapter(userId);
        adapter.updateItemQuantity(bookId, quantity);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long bookId) {
        CartAdapter adapter = cartAdapterFactory.createDatabaseCartAdapter(userId);
        adapter.removeItem(bookId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        CartAdapter adapter = cartAdapterFactory.createDatabaseCartAdapter(userId);
        adapter.clear();
    }

    @Override
    public List<CartItem> getCartItemsByUserId(Long userId) {
        CartAdapter adapter = cartAdapterFactory.createDatabaseCartAdapter(userId);
        return adapter.getItems();
    }

    @Override
    @Transactional
    public void mergeWithSessionCart(Long userId, Cart sessionCart) {
        if (sessionCart != null && !sessionCart.getItems().isEmpty()) {
            CartAdapter dbAdapter = cartAdapterFactory.createDatabaseCartAdapter(userId);
            CartAdapter sessionAdapter = cartAdapterFactory.createSessionCartAdapter(sessionCart);
            
            for (CartItem item : sessionAdapter.getItems()) {
                dbAdapter.addItem(item.getBook().getId(), item.getBook(), item.getQuantity());
            }
        }
    }

    @Override
    public Cart getUserCart(Long userId) {
        CartAdapter adapter = cartAdapterFactory.createDatabaseCartAdapter(userId);
        return adapter.toCart();
    }
}
