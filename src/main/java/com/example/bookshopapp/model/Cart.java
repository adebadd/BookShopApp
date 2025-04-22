package com.example.bookshopapp.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void addItem(Book book, int quantity) {
        for (CartItem item : items) {
            if (item.getBook().getId().equals(book.getId())) {
                item.setQuantity(quantity);
                return;
            }
        }
        CartItem newItem = new CartItem();
        newItem.setBook(book);
        newItem.setQuantity(quantity);
        items.add(newItem);
    }

    public void updateItemQuantity(Long bookId, int quantity) {
        for (CartItem item : items) {
            if (item.getBook().getId().equals(bookId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void removeItem(Long bookId) {
        items.removeIf(item -> item.getBook().getId().equals(bookId));
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : items) {
            subtotal = subtotal.add(item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        return subtotal;
    }

    public void clear() {
        items.clear();
    }
}
