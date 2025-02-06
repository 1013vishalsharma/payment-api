package com.hitpixel.payment.repository;

import com.hitpixel.payment.domain.Transaction;
import com.hitpixel.payment.domain.User;
import com.hitpixel.payment.enums.Currency;
import com.hitpixel.payment.enums.PaymentStatus;
import com.hitpixel.payment.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("1", "Harry", "harry@gmail.com", "password123");
        userRepository.save(user);

        Transaction transaction1 = new Transaction("T1", new BigDecimal("100.00"), PaymentMethod.CREDIT_CARD, Currency.USD, user, PaymentStatus.SUCCESS, LocalDateTime.now());
        Transaction transaction2 = new Transaction("T2", new BigDecimal("200.00"), PaymentMethod.CREDIT_CARD, Currency.USD, user, PaymentStatus.SUCCESS, LocalDateTime.now());
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
    }

    @Test
    void testFindAllByUserId() {
        List<Transaction> transactions = transactionRepository.findAllByUserId(user.getId());

        assertNotNull(transactions);
        assertEquals(2, transactions.size()); // Two transactions should be found for this user
        assertTrue(transactions.stream().anyMatch(transaction -> transaction.getId().equals("T1")));
        assertTrue(transactions.stream().anyMatch(transaction -> transaction.getId().equals("T2")));
    }

    @Test
    void testFindAllByUserIdNoTransactions() {
        User newUser = new User("2", "Gandalf", "gandalf@gmail.com", "password456");
        userRepository.save(newUser);

        List<Transaction> transactions = transactionRepository.findAllByUserId(newUser.getId());

        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void testFindAllByUserIdWithNonExistentUser() {
        List<Transaction> transactions = transactionRepository.findAllByUserId("abc");

        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void testSaveAndRetrieveTransaction() {
        Transaction newTransaction = new Transaction("T3", new BigDecimal("50.00"), PaymentMethod.CREDIT_CARD, Currency.USD, user, PaymentStatus.SUCCESS, LocalDateTime.now());
        transactionRepository.save(newTransaction);

        List<Transaction> transactions = transactionRepository.findAllByUserId(user.getId());

        assertNotNull(transactions);
        assertEquals(3, transactions.size());
        assertTrue(transactions.stream().anyMatch(transaction -> transaction.getId().equals("T3")));
    }
}
