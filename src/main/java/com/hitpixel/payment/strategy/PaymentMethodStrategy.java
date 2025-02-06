package com.hitpixel.payment.strategy;

import com.hitpixel.payment.domain.Transaction;
import com.hitpixel.payment.dto.Payment;
import com.hitpixel.payment.enums.PaymentMethod;

public interface PaymentMethodStrategy {

    /**
     * Payment method for the payment strategy
     * @return Payment method
     */
    PaymentMethod getPaymentMethod();

    /**
     * Method to process payment for the specific payment type like credit card or paypal
     * @param payment payment to be processed
     * @return transaction object
     */
    Transaction processPayment(Payment payment);

}