package com.example.library;

import com.example.library.model.BorrowRecord;
import com.example.library.model.Book;
import com.example.library.model.User;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowService {
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    public String borrowBook(Long bookId, String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (userOpt.isEmpty() || bookOpt.isEmpty()) return "User or Book not found";
        // Check if book is already borrowed and not returned
        if (!borrowRecordRepository.findByBookIdAndReturnDateIsNull(bookId).isEmpty()) {
            return "Book is already borrowed";
        }
        BorrowRecord record = new BorrowRecord();
        record.setUser(userOpt.get());
        record.setBook(bookOpt.get());
        record.setBorrowDate(LocalDate.now());
        borrowRecordRepository.save(record);
        return "Book borrowed successfully";
    }

    public String returnBook(Long bookId, String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) return "User not found";
        List<BorrowRecord> records = borrowRecordRepository.findByUserIdAndBookIdAndReturnDateIsNull(userOpt.get().getId(), bookId);
        if (records.isEmpty()) return "No active borrow record found";
        BorrowRecord record = records.get(0);
        record.setReturnDate(LocalDate.now());
        borrowRecordRepository.save(record);
        return "Book returned successfully";
    }

    public List<BorrowRecord> getUserHistory(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        return userOpt.map(user -> borrowRecordRepository.findByUserId(user.getId())).orElse(List.of());
    }

    public List<BorrowRecord> getAllRecords() {
        return borrowRecordRepository.findAll();
    }
} 