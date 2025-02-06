package com.hitpixel.payment.controller;

import com.hitpixel.payment.domain.Transaction;
import com.hitpixel.payment.domain.User;
import com.hitpixel.payment.dto.AuthenticationToken;
import com.hitpixel.payment.dto.Payment;
import com.hitpixel.payment.enums.Currency;
import com.hitpixel.payment.enums.PaymentMethod;
import com.hitpixel.payment.enums.PaymentStatus;
import com.hitpixel.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)

class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    private Payment testPayment;
    private Transaction testTransaction;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new com.hitpixel.payment.domain.User("1", "Test User", "testuser@example.com", "password123");
        testPayment = new Payment(BigDecimal.valueOf(100.0), PaymentMethod.CREDIT_CARD, Currency.USD);
        testTransaction = new Transaction("txn123", BigDecimal.valueOf(100), PaymentMethod.CREDIT_CARD, Currency.USD, testUser, PaymentStatus.SUCCESS, LocalDateTime.now());

        AuthenticationToken authenticationToken = Mockito.mock(AuthenticationToken.class);
        when(authenticationToken.getUser()).thenReturn(testUser);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testMakePayments_Success() throws Exception {
        when(paymentService.makePayment(testPayment, testUser)).thenReturn(testTransaction);

        mockMvc.perform(post("/api/payments")
                .contentType("application/json")
                .content("""
                        {
                            "paymentAmount": 100.0,
                            "paymentMethod": "CREDIT_CARD",
                            "currency": "USD"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("txn123"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentService, times(1)).makePayment(testPayment, testUser);
    }

    @Test
    void testFetchTransactions_Success() throws Exception {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(paymentService.fetchTransactions(testUser)).thenReturn(transactions);

        mockMvc.perform(get("/api/payments/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("txn123"))
                .andExpect(jsonPath("$[0].amount").value(100))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"));

        verify(paymentService, times(1)).fetchTransactions(testUser);
    }

    @Test
    void testGetPaymentStatus_Success() throws Exception {
        when(paymentService.fetchTransactionStatus("txn123", testUser)).thenReturn(PaymentStatus.SUCCESS);

        mockMvc.perform(get("/api/payments/{transactionId}/status", "txn123"))
                .andExpect(status().isOk());
        verify(paymentService, times(1)).fetchTransactionStatus("txn123", testUser);
    }

    @Test
    void testRefundPayment_Success() throws Exception {
        testTransaction.setStatus(PaymentStatus.REFUNDED);
        when(paymentService.refundTransaction("txn123", testUser)).thenReturn(testTransaction);

        mockMvc.perform(post("/api/payments/{transactionId}/refund", "txn123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("txn123"))
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        verify(paymentService, times(1)).refundTransaction("txn123", testUser);
    }
}