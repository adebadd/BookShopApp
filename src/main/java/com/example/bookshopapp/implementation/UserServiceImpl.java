package com.example.bookshopapp.implementation;

import com.example.bookshopapp.model.User;
import com.example.bookshopapp.repository.UserRepository;
import com.example.bookshopapp.service.AuthenticationStrategy;
import com.example.bookshopapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationStrategy authenticationStrategy;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AuthenticationStrategy authenticationStrategy, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationStrategy = authenticationStrategy;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        if (user.getId() == null && user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> authenticate(String email, String password) {
        return authenticationStrategy.authenticate(email, password);
    }

    @Override
    public boolean register(User user, String password) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    @Override
    public User updateUser(User updatedUser, Long id) {
        Optional<User> existingUserOpt = userRepository.findById(id);

        if (existingUserOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }

        User existingUser = existingUserOpt.get();

        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setStreetAddress(updatedUser.getStreetAddress());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setState(updatedUser.getState());
        existingUser.setPostalCode(updatedUser.getPostalCode());
        existingUser.setCountry(updatedUser.getCountry());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }

        if (updatedUser.isActive() != existingUser.isActive()) {
            existingUser.setActive(updatedUser.isActive());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public boolean existsByEmailAndNotId(String email, Long userId) {
        return userRepository.existsByEmailAndIdNot(email, userId);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }
}