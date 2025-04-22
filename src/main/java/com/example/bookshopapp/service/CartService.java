package com.example.bookshopapp.service;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Cart;
import com.example.bookshopapp.model.CartItem;

import java.util.List;

public interface CartService {

    void addCartItem(Long userId, Long bookId, Integer quantity);
    void updateCartItemQuantity(Long userId, Long bookId, Integer quantity);
    void removeCartItem(Long userId, Long bookId);
    void clearCart(Long userId);
    List<CartItem> getCartItemsByUserId(Long userId);

    void mergeWithSessionCart(Long userId, Cart sessionCart);
    Cart getUserCart(Long userId);
}
