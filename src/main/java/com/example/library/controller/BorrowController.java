package com.example.library.controller;

import com.example.library.BorrowService;
import com.example.library.model.BorrowRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    @PostMapping("/borrow/{bookId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> borrowBook(@PathVariable Long bookId, Principal principal) {
        String result = borrowService.borrowBook(bookId, principal.getName());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/return/{bookId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> returnBook(@PathVariable Long bookId, Principal principal) {
        String result = borrowService.returnBook(bookId, principal.getName());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/borrow/history")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<BorrowRecord>> getUserHistory(Principal principal) {
        List<BorrowRecord> history = borrowService.getUserHistory(principal.getName());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/borrow/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BorrowRecord>> getAllRecords() {
        return ResponseEntity.ok(borrowService.getAllRecords());
    }
} 