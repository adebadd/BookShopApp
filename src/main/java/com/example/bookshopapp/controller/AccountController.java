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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {
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
        
        User user = userOpt.get();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setStreetAddress(streetAddress);
        user.setCity(city);
        user.setState(state);
        user.setPostalCode(postalCode);
        user.setCountry(country);
        
        try {
            User updatedUser = userService.updateUser(user, userId);
            session.setAttribute("userId", updatedUser.getId());
            session.setAttribute("userEmail", updatedUser.getEmail());
            session.setAttribute("user", updatedUser);
            
            redirectAttributes.addFlashAttribute("successMessage", "Your account details have been updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update account: " + e.getMessage());
        }
        return "redirect:/account";
    }
}