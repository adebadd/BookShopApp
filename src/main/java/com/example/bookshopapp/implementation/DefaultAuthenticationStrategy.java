package com.example.bookshopapp.implementation;

import com.example.bookshopapp.model.User;
import com.example.bookshopapp.repository.UserRepository;
import com.example.bookshopapp.service.AuthenticationStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultAuthenticationStrategy implements AuthenticationStrategy {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public DefaultAuthenticationStrategy(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public Optional<User> authenticate(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }

        String trimmedEmail = email.trim();
        Optional<User> userOptional = userRepository.findByEmail(trimmedEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}
