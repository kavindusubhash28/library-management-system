package com.example.library.repository;

import com.example.library.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserId(Long userId);
    List<BorrowRecord> findByBookIdAndReturnDateIsNull(Long bookId);
    List<BorrowRecord> findByUserIdAndBookIdAndReturnDateIsNull(Long userId, Long bookId);
} 