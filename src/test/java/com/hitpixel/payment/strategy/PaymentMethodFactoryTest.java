package com.hitpixel.payment.strategy;

import com.hitpixel.payment.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PaymentMethodFactoryTest {

    private PaymentMethodFactory paymentMethodFactory;

    @Mock
    private PaymentMethodStrategy creditCardStrategy;

    @Mock
    private PaymentMethodStrategy paypalStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(creditCardStrategy.getPaymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(paypalStrategy.getPaymentMethod()).thenReturn(PaymentMethod.PAY_PAL);
        paymentMethodFactory = new PaymentMethodFactory(List.of(creditCardStrategy, paypalStrategy));
    }

    @Test
    void testGetPaymentMethodStrategy_CreditCard() {
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        when(creditCardStrategy.getPaymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);

        PaymentMethodStrategy strategy = paymentMethodFactory.getPaymentMethodStrategy(paymentMethod);

        assertNotNull(strategy);
        assertEquals(creditCardStrategy, strategy);
    }

    @Test
    void testGetPaymentMethodStrategy_Paypal() {
        PaymentMethod paymentMethod = PaymentMethod.PAY_PAL;
        when(paypalStrategy.getPaymentMethod()).thenReturn(PaymentMethod.PAY_PAL);

        PaymentMethodStrategy strategy = paymentMethodFactory.getPaymentMethodStrategy(paymentMethod);

        assertNotNull(strategy);
        assertEquals(paypalStrategy, strategy);
    }

    @Test
    void testGetPaymentMethodStrategy_NullPaymentMethod() {
        PaymentMethodStrategy strategy = paymentMethodFactory.getPaymentMethodStrategy(null);

        assertNull(strategy, "Strategy should be null for null payment method");
    }
}
