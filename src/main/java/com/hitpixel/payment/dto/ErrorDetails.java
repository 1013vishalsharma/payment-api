package com.hitpixel.payment.dto;

import java.time.LocalDateTime;

public record ErrorDetails(
        String message,
        int status,
        LocalDateTime timestamp,
        String error
) {
}
