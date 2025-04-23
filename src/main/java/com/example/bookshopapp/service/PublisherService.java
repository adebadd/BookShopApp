package com.example.bookshopapp.service;

import com.example.bookshopapp.model.Publisher;
import java.util.List;
import java.util.Optional;

public interface PublisherService {
    List<Publisher> getAllPublishers();
    Optional<Publisher> getPublisherById(Long id);
    Publisher savePublisher(Publisher publisher);
    void deletePublisher(Long id);
}
