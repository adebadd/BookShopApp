package com.example.bookshopapp.service;

import com.example.bookshopapp.model.User;
import java.util.Optional;

public interface AuthenticationStrategy {
    Optional<User> authenticate(String email, String password);
}
