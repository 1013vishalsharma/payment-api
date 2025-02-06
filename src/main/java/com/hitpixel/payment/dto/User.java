package com.hitpixel.payment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record User(

        @NotBlank(message = "Name cannot be null or empty")
        String name,
        @NotBlank(message = "email cannot be null or empty")
        @Email(message = "Please enter a valid email", regexp = "^\\S+@\\S+$")
        String email,
        @NotBlank(message = "Password cannot be blank")
        String password) {
}
