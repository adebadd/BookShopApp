package com.example.bookshopapp.service;

import com.example.bookshopapp.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    List<User> getAllUsers();
    Page<User> getAllUsers(Pageable pageable);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User saveUser(User user);
    void deleteUser(Long id);
    Optional<User> authenticate(String email, String password);
    boolean register(User user, String password);
    User updateUser(User user, Long userId);
    boolean existsByEmailAndNotId(String email, Long userId);
    long countUsers();
}