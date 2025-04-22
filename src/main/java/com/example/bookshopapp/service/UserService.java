package com.example.bookshopapp.service;

import com.example.bookshopapp.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User saveUser(User user);
    void deleteUser(Long id);
    Optional<User> authenticate(String email, String password);
    boolean register(User user, String password);
    User updateUser(User user, Long userId);
    boolean existsByEmailAndNotId(String email, Long userId);
}