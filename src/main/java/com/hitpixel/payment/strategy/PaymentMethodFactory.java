package com.hitpixel.payment.strategy;

import com.hitpixel.payment.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;

@Component
public class PaymentMethodFactory {

    private EnumMap<PaymentMethod, PaymentMethodStrategy> paymentMethodMap;

    public PaymentMethodFactory(List<PaymentMethodStrategy> paymentMethodStrategies) {
        paymentMethodMap = new EnumMap<>(PaymentMethod.class);
        paymentMethodStrategies.stream().forEach(paymentMethodStrategy -> paymentMethodMap
                .put(paymentMethodStrategy.getPaymentMethod(), paymentMethodStrategy));
    }

    /**
     * Method to fetch payment strategy based on payment method.
     * @param paymentMethod Payment method used for payment
     * @return PaymentStrategy object
     */
    public PaymentMethodStrategy getPaymentMethodStrategy(PaymentMethod paymentMethod) {
        return paymentMethodMap.get(paymentMethod);
    }

}