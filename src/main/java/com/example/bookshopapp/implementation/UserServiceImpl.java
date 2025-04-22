package com.example.bookshopapp.implementation;

import com.example.bookshopapp.model.User;
import com.example.bookshopapp.repository.UserRepository;
import com.example.bookshopapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
            user.setPassword(encoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (encoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean register(User user, String password) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return false;
        }
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        return true;
    }

    @Override
    public User updateUser(User updatedUser, long id) {
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
            existingUser.setPassword(encoder.encode(updatedUser.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }
}