package com.example.demo.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("""
             SELECT history
             FROM BookTransactionHistory history
             WHERE history.user.id = :userId
             """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userid);


    @Query("""
             SELECT history
             FROM BookTransactionHistory history
             WHERE history.book.owner.id = :userId
             """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);


    @Query("""
             SELECT 
             (COUNT(*) > 0) AS isBorrowed
              FROM BookTransactionHistory bookTransactionHistory
              WHERE bookTransactionHistory.book.owner.id = :userId
              AND bookTransactionHistory.book.id = :bookId
              AND bookTransactionHistory.returnApproved = false
             """)
    Boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

    @Query("""
             SELECT transaction
             From BookTransactionHistory transaction
             Where transaction.user.id = :userId
             AND transaction.book.id = :bookId
             AND transaction.returned = false
             AND transaction.returnApproved = false
             
             """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);

    @Query("""
             SELECT transaction
             From BookTransactionHistory transaction
             Where transaction.book.owner.id = :userId
             AND transaction.book.id = :bookId
             AND transaction.returned = true
             AND transaction.returnApproved = false     
             """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer id);
}
