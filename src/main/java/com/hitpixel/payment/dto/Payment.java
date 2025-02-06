package com.hitpixel.payment.dto;

import com.hitpixel.payment.enums.Currency;
import com.hitpixel.payment.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record Payment(
        @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount should be greater than 0.0")
        BigDecimal paymentAmount,
        @NotNull(message = "Payment method cannot be blank")
        PaymentMethod paymentMethod,
        @NotNull(message = "Currency is mandatory")
        Currency currency
) {
}
