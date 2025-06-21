package com.example.library.controller;

import com.example.library.model.Book;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @GetMapping
    public List<Book> getAllBooks() {
        // Return an empty list for now
        return List.of();
    }
} 