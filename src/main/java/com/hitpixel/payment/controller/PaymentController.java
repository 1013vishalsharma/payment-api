package com.hitpixel.payment.controller;

import com.hitpixel.payment.domain.Transaction;
import com.hitpixel.payment.domain.User;
import com.hitpixel.payment.dto.AuthenticationToken;
import com.hitpixel.payment.dto.ErrorDetails;
import com.hitpixel.payment.dto.Payment;
import com.hitpixel.payment.enums.PaymentStatus;
import com.hitpixel.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@SecurityRequirement(name = "Authorization")
public class PaymentController {

    PaymentService paymentService;
    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @Operation(summary = "Make payments to another entity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment completed successfully"),
            @ApiResponse(responseCode = "401", description = "User cannot be authenticated", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "User does not exists in the system", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "Request body is incorrect", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "500", description = "Error occurred while processing the request", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping
    public ResponseEntity<Transaction> makePayments(@Valid @RequestBody Payment payment) {
        User user = ((AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUser();
        log.info("Received payment request for user={}", user.getEmail());
        Transaction transaction = paymentService.makePayment(payment, user);
        log.info("Payment processed for user={}", user.getEmail());
        return ResponseEntity.ok(transaction);
    }

    @Operation(summary = "View transaction history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched transactions successfully"),
            @ApiResponse(responseCode = "401", description = "User cannot be authenticated", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "User does not exists in the system", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "Request body is incorrect", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "500", description = "Error occurred while processing the request", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @GetMapping("history")
    public ResponseEntity<List<Transaction>> fetchTransactions() {
        User user = ((AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUser();
        log.info("Fetching all transactions for user={}", user.getEmail());
        List<Transaction> transactions = paymentService.fetchTransactions(user);
        log.info("Fetched transactions for user={}", user.getEmail());
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "View transaction status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched Payment status successfully"),
            @ApiResponse(responseCode = "401", description = "User cannot be authenticated", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "User or transaction does not exists in the system", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "Request body is incorrect", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "500", description = "Error occurred while processing the request", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @GetMapping("{transactionId}/status")
    public ResponseEntity<PaymentStatus> getPaymentStatus(@PathVariable String transactionId) {
        User user = ((AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUser();
        log.info("Fetching status for transaction id={} and user={}", transactionId, user.getEmail());
        PaymentStatus transactionStatus = paymentService.fetchTransactionStatus(transactionId, user);
        log.info("Fetched status for transaction id={} and user={}", transactionId, user.getEmail());
        return ResponseEntity.ok(transactionStatus);
    }

    @Operation(summary = "Refund Payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment refunded successfully"),
            @ApiResponse(responseCode = "401", description = "User cannot be authenticated", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "User or transaction does not exists in the system", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "Request body is incorrect", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "500", description = "Error occurred while processing the request", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("{transactionId}/refund")
    public ResponseEntity<Transaction> refundPayment(@PathVariable String transactionId) {
        User user = ((AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUser();
        log.info("Refund requested for transaction id={} for user={}", transactionId, user.getEmail());
        Transaction transaction = paymentService.refundTransaction(transactionId, user);
        log.info("Refund completed for transaction id={} and user={}", transactionId, user.getEmail());
        return ResponseEntity.ok(transaction);
    }
}
