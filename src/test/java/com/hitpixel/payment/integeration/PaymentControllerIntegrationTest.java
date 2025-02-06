package com.hitpixel.payment.integeration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitpixel.payment.domain.Transaction;
import com.hitpixel.payment.dto.JWTAuthToken;
import com.hitpixel.payment.dto.LoginRequest;
import com.hitpixel.payment.dto.Payment;
import com.hitpixel.payment.dto.User;
import com.hitpixel.payment.enums.Currency;
import com.hitpixel.payment.enums.PaymentMethod;
import com.hitpixel.payment.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PaymentControllerIntegrationTest {

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Payment payment;

    private String url;

    private String userUrl;
    private LoginRequest loginRequest;
    private String authToken;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        user = new User("Arya Stark", "arya.stark@gmail.com", "1234567890");
        payment = new Payment(BigDecimal.valueOf(100.00), PaymentMethod.CREDIT_CARD, Currency.USD);
        userUrl = "http://localhost:" + port +"/api/users/";
        loginRequest = new LoginRequest("arya.stark@gmail.com", "1234567890");

        restTemplate.postForEntity(userUrl + "register", user, Void.class);

        ResponseEntity<JWTAuthToken> jwtAuthTokenResponseEntity = restTemplate.postForEntity(userUrl + "login", loginRequest, JWTAuthToken.class);
        authToken = jwtAuthTokenResponseEntity.getBody().token();
        url = "http://localhost:" + port + "/api/payments";
    }

    @Test
    void testMakePayment_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        HttpEntity<Payment> entity = new HttpEntity<>(payment, headers);

        ResponseEntity<Transaction> response = restTemplate.postForEntity(
                url, entity, Transaction.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void testFetchTransactionHistory_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        HttpEntity<Payment> entity = new HttpEntity<>(payment, headers);

        restTemplate.postForEntity(
                url, entity, Transaction.class);
        restTemplate.postForEntity(
                url, entity, Transaction.class);

        ResponseEntity<List> response = restTemplate.exchange(
                url + "/history", HttpMethod.GET, entity, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetPaymentStatus_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        HttpEntity<Payment> entity = new HttpEntity<>(payment, headers);

        ResponseEntity<Transaction> transactionResponse = restTemplate.postForEntity(
                url, entity, Transaction.class);
        String transactionId = transactionResponse.getBody().getId();

        ResponseEntity<PaymentStatus> response = restTemplate.exchange(
                url + "/{transactionId}/status", HttpMethod.GET, entity, PaymentStatus.class, transactionId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void testRefundPayment_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        HttpEntity<Payment> entity = new HttpEntity<>(payment, headers);

        ResponseEntity<Transaction> transactionResponse = restTemplate.postForEntity(
                url, entity, Transaction.class);
        String transactionId = transactionResponse.getBody().getId();

        ResponseEntity<Transaction> response = restTemplate.exchange(
                url + "/{transactionId}/refund", HttpMethod.POST, entity, Transaction.class, transactionId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }

    @Test
    void testGetPaymentStatus_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        HttpEntity<Payment> entity = new HttpEntity<>(payment, headers);
        String transactionId = "random-id";

        ResponseEntity<String> response = restTemplate.exchange(
                url + "/{transactionId}/status", HttpMethod.GET, entity, String.class, transactionId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Transaction does not exists");
    }

    @Test
    void testRefundPayment_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        HttpEntity<Payment> entity = new HttpEntity<>(payment, headers);
        String transactionId = "random-id";

        ResponseEntity<String> response = restTemplate.exchange(
                url + "/{transactionId}/refund", HttpMethod.POST, entity, String.class, transactionId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Transaction does not exists");
    }
}
