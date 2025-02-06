package com.hitpixel.payment.strategy;

import com.hitpixel.payment.domain.Transaction;
import com.hitpixel.payment.dto.Payment;
import com.hitpixel.payment.enums.PaymentMethod;
import com.hitpixel.payment.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class PayPalPaymentStrategy implements PaymentMethodStrategy {
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.PAY_PAL;
    }

    /*
        This method simulates paypal payment
     */
    @Override
    public Transaction processPayment(Payment payment) {
        log.info("Processing PayPal payment");
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setAmount(payment.paymentAmount());
        transaction.setPaymentMethod(payment.paymentMethod());
        transaction.setCurrency(payment.currency());
        transaction.setStatus(PaymentStatus.SUCCESS);
        transaction.setTransactionTimestamp(LocalDateTime.now());
        log.info("Completed processing of PayPal payment");
        return transaction;
    }
}
