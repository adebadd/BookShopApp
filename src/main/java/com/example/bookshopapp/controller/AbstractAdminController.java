package com.example.bookshopapp.controller;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

public abstract class AbstractAdminController {
    protected final String executeAdminOperation(HttpSession session, OperationCallback callback) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/login";
        }
        logOperation();
        String result = callback.execute();
        performPostOperation();

        return result;
    }

    protected boolean isAdminAuthenticated(HttpSession session) {
        return session.getAttribute("userRole") != null &&
                session.getAttribute("userRole").toString().equals("ADMIN");
    }

    protected void logOperation() {
    }

    protected void performPostOperation() {
    }

    @FunctionalInterface
    protected interface OperationCallback {
        String execute();
    }
}
