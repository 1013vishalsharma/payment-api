package com.hitpixel.payment.strategy;

import com.hitpixel.payment.domain.Transaction;
import com.hitpixel.payment.dto.Payment;
import com.hitpixel.payment.enums.Currency;
import com.hitpixel.payment.enums.PaymentMethod;
import com.hitpixel.payment.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PayPalPaymentStrategyTest {
    private PayPalPaymentStrategy payPalPaymentStrategy;

    @BeforeEach
    void setUp() {
        payPalPaymentStrategy = new PayPalPaymentStrategy();
    }

    @Test
    void testGetPaymentMethod() {
        PaymentMethod paymentMethod = payPalPaymentStrategy.getPaymentMethod();

        assertEquals(PaymentMethod.PAY_PAL, paymentMethod);
    }

    @Test
    void testProcessPayment() {
        Payment payment = mock(Payment.class);
        when(payment.paymentAmount()).thenReturn(BigDecimal.valueOf(100.00));
        when(payment.paymentMethod()).thenReturn(PaymentMethod.PAY_PAL);
        when(payment.currency()).thenReturn(Currency.valueOf("USD"));

        Transaction transaction = payPalPaymentStrategy.processPayment(payment);

        assertNotNull(transaction);
        assertNotNull(transaction.getId());
        assertEquals(BigDecimal.valueOf(100.00), transaction.getAmount());
        assertEquals(PaymentMethod.PAY_PAL, transaction.getPaymentMethod());
        assertEquals(Currency.valueOf("USD"), transaction.getCurrency());
        assertEquals(PaymentStatus.SUCCESS, transaction.getStatus());
        assertNotNull(transaction.getTransactionTimestamp());
    }

    @Test
    void testTransactionIdGeneration() {
        Payment payment = mock(Payment.class);
        when(payment.paymentAmount()).thenReturn(BigDecimal.valueOf(100.00));
        when(payment.paymentMethod()).thenReturn(PaymentMethod.PAY_PAL);
        when(payment.currency()).thenReturn(Currency.valueOf("USD"));

        Transaction transaction1 = payPalPaymentStrategy.processPayment(payment);
        Transaction transaction2 = payPalPaymentStrategy.processPayment(payment);

        assertNotEquals(transaction1.getId(), transaction2.getId());
    }

    @Test
    void testProcessPaymentWithNullPayment() {
        assertThrows(NullPointerException.class, () -> payPalPaymentStrategy.processPayment(null),
                "Should throw NullPointerException when payment is null");
    }
}
