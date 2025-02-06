package com.hitpixel.payment.repository;

import com.hitpixel.payment.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Find all transactions by user id
     * @param id user id
     * @return List of all transaction for a user.
     */
    List<Transaction> findAllByUserId(String id);
}
