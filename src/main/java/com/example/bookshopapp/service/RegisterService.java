package com.example.bookshopapp.service;

public interface RegisterService {
    void registerUser(String email, String password, String firstName, String lastName, String phone);
}