package com.example.bookshopapp.controller;

import com.example.bookshopapp.model.Order;
import com.example.bookshopapp.model.User;
import com.example.bookshopapp.service.OrderService;
import com.example.bookshopapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("")
    public String showAccountPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            session.setAttribute("redirectAfterLogin", "/account");
            return "redirect:/login";
        }
        Optional<User> userOpt = userService.getUserById(userId);
        if (!userOpt.isPresent()) {
            return "redirect:/login";
        }
        User user = userOpt.get();
        model.addAttribute("user", user);
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("phone", user.getPhone());
        model.addAttribute("streetAddress", user.getStreetAddress());
        model.addAttribute("city", user.getCity());
        model.addAttribute("state", user.getState());
        model.addAttribute("postalCode", user.getPostalCode());
        model.addAttribute("country", user.getCountry());
        model.addAttribute("preferredPaymentMethod", user.getPreferredPaymentMethod());
        List<Order> userOrders = orderService.getOrdersByUserId(userId);
        model.addAttribute("orders", userOrders);
        return "account";
    }
    
    @PostMapping("")
    public String updateUserDetails(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("streetAddress") String streetAddress,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("postalCode") String postalCode,
            @RequestParam("country") String country,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (!userOpt.isPresent()) {
            return "redirect:/login";
        }
        
        User currentUser = userOpt.get();
        String originalEmail = currentUser.getEmail();
        
        String existingPassword = currentUser.getPassword();

        User updatedUser = new User();
        updatedUser.setId(currentUser.getId());
        updatedUser.setPassword(existingPassword);

        updatedUser.setFirstName(firstName != null ? firstName.trim() : currentUser.getFirstName());
        updatedUser.setLastName(lastName != null ? lastName.trim() : currentUser.getLastName());
        updatedUser.setEmail(email != null ? email.trim() : currentUser.getEmail());
        updatedUser.setPhone(phone != null ? phone.trim() : currentUser.getPhone());
        updatedUser.setStreetAddress(streetAddress != null ? streetAddress.trim() : currentUser.getStreetAddress());
        updatedUser.setCity(city != null ? city.trim() : currentUser.getCity());
        updatedUser.setState(state != null ? state.trim() : currentUser.getState());
        updatedUser.setPostalCode(postalCode != null ? postalCode.trim() : currentUser.getPostalCode());
        updatedUser.setCountry(country != null ? country.trim() : currentUser.getCountry());
        
        updatedUser.setPreferredPaymentMethod(currentUser.getPreferredPaymentMethod());
        updatedUser.setRole(currentUser.getRole());
        
        boolean emailChanged = !originalEmail.equals(updatedUser.getEmail());
        if (emailChanged) {
            if (userService.existsByEmailAndNotId(updatedUser.getEmail(), userId)) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "This email is already in use by another account. Please use a different email.");
                return "redirect:/account";
            }
        }
        
        try {
            User savedUser = userService.updateUser(updatedUser, userId);
            
            session.setAttribute("userId", savedUser.getId());
            session.setAttribute("userEmail", savedUser.getEmail());
            
            if (session.getAttribute("user") != null) {
                session.setAttribute("user", savedUser);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Your account details have been updated successfully." + 
                (emailChanged ? " Please note that you will need to use your new email address to log in next time." : ""));
            
        } catch (Exception e) {
            logger.error("Error updating user {}: {}", userId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to update account. Please check your information and try again.");
        }
        return "redirect:/account";
    }
}