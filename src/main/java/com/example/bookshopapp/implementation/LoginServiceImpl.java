package com.example.bookshopapp.implementation;

import com.example.bookshopapp.model.User;
import com.example.bookshopapp.repository.UserRepository;
import com.example.bookshopapp.service.LoginService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    public LoginServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean authenticate(String email, String password) {
        if (email == null || password == null) {
            logger.debug("Email or password is null");
            return false;
        }
        
        String trimmedEmail = email.trim();
        logger.debug("Authenticating user: {}", trimmedEmail);
        
        Optional<User> userOptional = userRepository.findByEmail(trimmedEmail);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.debug("User found: {}", user.getEmail());
            
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            logger.debug("Password match result: {}", matches);
            
            return matches;
        }

        logger.debug("No user found with exact email match, performing manual check");
        
        logger.debug("No user found with email: {}", trimmedEmail);
        return false;
    }
}