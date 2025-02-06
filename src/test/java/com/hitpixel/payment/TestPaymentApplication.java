package com.hitpixel.payment;

import org.springframework.boot.SpringApplication;

public class TestPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.from(PaymentApplication::main).run(args);
	}

}
