package com.example.bookshopapp.controller;

import com.example.bookshopapp.model.Book;
import com.example.bookshopapp.model.Cart;
import com.example.bookshopapp.model.Order;
import com.example.bookshopapp.model.OrderItem;
import com.example.bookshopapp.model.User;
import com.example.bookshopapp.model.OrderBuilder;
import com.example.bookshopapp.model.OrderItemBuilder;
import com.example.bookshopapp.service.BookService;
import com.example.bookshopapp.service.OrderService;
import com.example.bookshopapp.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Controller
public class CheckoutController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("user", user);
                model.addAttribute("firstName", user.getFirstName());
                model.addAttribute("lastName", user.getLastName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("phone", user.getPhone());
                model.addAttribute("address", user.getStreetAddress());
                model.addAttribute("city", user.getCity());
                model.addAttribute("state", user.getState());
                model.addAttribute("postcode", user.getPostalCode());
                model.addAttribute("country", user.getCountry());
                model.addAttribute("paymentMethod", user.getPreferredPaymentMethod());
            }
        }
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("subtotal", cart.getSubtotal());
        return "checkout";
    }

    @PostMapping("/checkout/process")
    @Transactional
    public String processCheckout(
            @RequestParam Map<String, String> params,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        String firstName = params.get("firstName");
        String lastName = params.get("lastName");
        String email = params.get("email");
        String phone = params.get("phone");
        String address = params.get("address");
        String city = params.get("city");
        String state = params.get("state");
        String postcode = params.get("postcode");
        String country = params.get("country");
        String paymentMethod = params.get("paymentMethod");
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to complete checkout.");
                return "redirect:/login";
            }
            Order order = new OrderBuilder()
                .withUserId(userId)
                .withTotalAmount(cart.getSubtotal())
                .withStatus("PENDING")
                .withShippingAddress(address)
                .withShippingCity(city)
                .withShippingState(state)
                .withShippingPostalCode(postcode)
                .withShippingCountry(country)
                .withPaymentMethod(paymentMethod)
                .build();
                
            for (com.example.bookshopapp.model.CartItem cartItem : cart.getItems()) {
                Book book = cartItem.getBook();
                if (!bookService.updateStock(book.getId(), -cartItem.getQuantity())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Not enough stock for " + book.getTitle());
                    return "redirect:/checkout";
                }
                
                OrderItem orderItem = new OrderItemBuilder()
                    .withOrder(order)
                    .withBook(book)
                    .withQuantity(cartItem.getQuantity())
                    .withUnitPrice(book.getPrice())
                    .build();
                    
                order.addOrderItem(orderItem);
            }

            order = orderService.saveOrder(order);
            session.setAttribute("lastOrder", order);
            String orderNumber = generateOrderNumber(order.getId());
            session.setAttribute("orderNumber", orderNumber);
            session.setAttribute("firstName", firstName);
            session.setAttribute("lastName", lastName);
            session.setAttribute("email", email);
            session.setAttribute("phone", phone);
            session.setAttribute("address", address);
            session.setAttribute("city", city);
            session.setAttribute("state", state);
            session.setAttribute("postcode", postcode);
            session.setAttribute("country", country);
            session.setAttribute("paymentMethod", paymentMethod);
            session.removeAttribute("cart");
            return "redirect:/order-confirmation";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred during checkout: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/order-confirmation")
    public String showOrderConfirmation(Model model, HttpSession session) {
        Order order = (Order) session.getAttribute("lastOrder");
        if (order == null) {
            return "redirect:/home";
        }
        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        model.addAttribute("orderNumber", session.getAttribute("orderNumber"));
        model.addAttribute("firstName", session.getAttribute("firstName"));
        model.addAttribute("lastName", session.getAttribute("lastName"));
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("phone", session.getAttribute("phone"));
        model.addAttribute("address", session.getAttribute("address"));
        model.addAttribute("city", session.getAttribute("city"));
        model.addAttribute("state", session.getAttribute("state"));
        model.addAttribute("postcode", session.getAttribute("postcode"));
        model.addAttribute("country", session.getAttribute("country"));
        model.addAttribute("paymentMethod", session.getAttribute("paymentMethod"));
        return "order-confirmation";
    }

    private String generateOrderNumber(Long orderId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = now.format(formatter);
        return String.format("ORD-%s-%03d", datePart, orderId);
    }
}