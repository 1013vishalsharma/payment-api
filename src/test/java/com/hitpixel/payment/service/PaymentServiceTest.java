package com.hitpixel.payment.service;

import com.hitpixel.payment.domain.*;
import com.hitpixel.payment.dto.Payment;
import com.hitpixel.payment.enums.PaymentMethod;
import com.hitpixel.payment.enums.PaymentStatus;
import com.hitpixel.payment.exception.TransactionNotExistsException;
import com.hitpixel.payment.repository.TransactionRepository;
import com.hitpixel.payment.strategy.PaymentMethodFactory;
import com.hitpixel.payment.strategy.PaymentMethodStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentMethodFactory paymentMethodFactory;

    @Mock
    private PaymentMethodStrategy paymentMethodStrategy;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentService(transactionRepository, paymentMethodFactory);
    }

    @Test
    void testMakePayment() {
        Payment payment = mock(Payment.class);
        User user = mock(User.class);
        Transaction transaction = new Transaction();
        transaction.setId("1234");
        transaction.setStatus(PaymentStatus.SUCCESS);

        when(payment.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(paymentMethodFactory.getPaymentMethodStrategy(PaymentMethod.CREDIT_CARD)).thenReturn(paymentMethodStrategy);
        when(paymentMethodStrategy.processPayment(payment)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = paymentService.makePayment(payment, user);

        assertNotNull(result);
        assertEquals("1234", result.getId());
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testFetchTransactions() {
        User user = mock(User.class);
        Transaction transaction = new Transaction();
        transaction.setId("1234");
        transaction.setStatus(PaymentStatus.SUCCESS);
        when(user.getId()).thenReturn("user123");
        when(transactionRepository.findAllByUserId("user123")).thenReturn(Collections.singletonList(transaction));

        List<Transaction> transactions = paymentService.fetchTransactions(user);

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals("1234", transactions.get(0).getId());
        verify(transactionRepository, times(1)).findAllByUserId("user123");
    }

    @Test
    void testFetchTransactions_withNoTransactions() {
        User user = mock(User.class);
        when(user.getId()).thenReturn("user123");
        when(transactionRepository.findAllByUserId("user123")).thenReturn(List.of());

        List<Transaction> transactions = paymentService.fetchTransactions(user);

        assertNotNull(transactions);
        assertEquals(0, transactions.size());
        verify(transactionRepository, times(1)).findAllByUserId("user123");
    }

    @Test
    void testFetchTransactionStatus() {
        String transactionId = "1234";
        User user = mock(User.class);
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setStatus(PaymentStatus.SUCCESS);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        PaymentStatus status = paymentService.fetchTransactionStatus(transactionId, user);

        assertEquals(PaymentStatus.SUCCESS, status);
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testFetchTransactionStatusThrowsException() {
        String transactionId = "1234";
        User user = mock(User.class);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotExistsException.class, () -> paymentService.fetchTransactionStatus(transactionId, user));
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testRefundTransaction() {
        String transactionId = "1234";
        User user = mock(User.class);
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setStatus(PaymentStatus.SUCCESS);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction refundedTransaction = paymentService.refundTransaction(transactionId, user);

        assertNotNull(refundedTransaction);
        assertEquals(PaymentStatus.REFUNDED, refundedTransaction.getStatus());
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testRefundTransactionAlreadyRefunded() {
        String transactionId = "1234";
        User user = mock(User.class);
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setStatus(PaymentStatus.REFUNDED);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        assertThrows(TransactionNotExistsException.class, () -> paymentService.refundTransaction(transactionId, user));
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testRefundTransactionThrowsExceptionWhenNotFound() {
        String transactionId = "1234";
        User user = mock(User.class);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotExistsException.class, () -> paymentService.refundTransaction(transactionId, user));
        verify(transactionRepository, times(1)).findById(transactionId);
    }
}
