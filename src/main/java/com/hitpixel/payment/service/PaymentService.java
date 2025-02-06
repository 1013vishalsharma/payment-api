package com.hitpixel.payment.service;

import com.hitpixel.payment.domain.Transaction;
import com.hitpixel.payment.domain.User;
import com.hitpixel.payment.dto.Payment;
import com.hitpixel.payment.enums.PaymentStatus;
import com.hitpixel.payment.exception.TransactionNotExistsException;
import com.hitpixel.payment.repository.TransactionRepository;
import com.hitpixel.payment.strategy.PaymentMethodFactory;
import com.hitpixel.payment.strategy.PaymentMethodStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PaymentService {
    private static final String TRANSACTION_DOES_NOT_EXISTS = "Transaction does not exists in the system";
    private final TransactionRepository transactionRepository;
    private final PaymentMethodFactory paymentMethodFactory;

    public PaymentService(TransactionRepository transactionRepository,
                          PaymentMethodFactory paymentMethodFactory) {
        this.transactionRepository = transactionRepository;
        this.paymentMethodFactory = paymentMethodFactory;
    }

    /**
     * Method to make payments to other entity.
     * @param payment payment details object
     * @param user initiating user
     * @return executed transaction object
     */
    @Transactional
    public Transaction makePayment(Payment payment, User user) {
        log.info("Processing payment for user={}", user.getEmail());
        PaymentMethodStrategy paymentMethodStrategy = paymentMethodFactory.getPaymentMethodStrategy(payment.paymentMethod());
        Transaction transaction = paymentMethodStrategy.processPayment(payment);
        transaction.setUser(user);
        Transaction persistedTransaction = transactionRepository.save(transaction);
        log.info("Transaction processed successfully");
        return persistedTransaction;
    }


    /**
     * Method to fetch all transactions for a user
     * @param user user for whom we are fetching the transactions
     * @return list of all transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> fetchTransactions(User user) {
        log.info("Fetching transactions for user={}", user.getEmail());
        List<Transaction> transactions = transactionRepository.findAllByUserId(user.getId());
        if(transactions.isEmpty()) {
            log.warn("No transactions found for user={}", user.getEmail());
        } else {
            log.info("successfully retrieved {} transaction(s) for user={}", transactions.size(), user.getEmail());
        }
        return transactions;
    }

    /**
     * Method to fetch status of a particular transaction
     * @param transactionId transactionId for which we have to retrieve the status
     * @param user currently logged in user
     * @return Payment status of the transaction
     */
    @Transactional(readOnly = true)
    public PaymentStatus fetchTransactionStatus(String transactionId, User user) {
        log.info("Fetching transaction status for transactionId={}" , transactionId);
        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() -> {
                    log.error(TRANSACTION_DOES_NOT_EXISTS);
                    throw new TransactionNotExistsException(TRANSACTION_DOES_NOT_EXISTS);
                });
        log.info("Successfully fetched transaction from the system");
        return transaction.getStatus();
    }

    /**
     * Method to refund the transaction
     * @param transactionId transactionId for the transaction to be refunded
     * @param user currently logged in user
     * @return refunded transaction
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Transaction refundTransaction(String transactionId, User user) {
        log.info("Initiating refund for transaction id={}", transactionId);
        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() -> {
                    log.error(TRANSACTION_DOES_NOT_EXISTS);
                    throw new TransactionNotExistsException(TRANSACTION_DOES_NOT_EXISTS);
                });
        if(transaction.getStatus().equals(PaymentStatus.REFUNDED)) {
            log.error("Transaction with transaction id ={} is already refunded", transactionId);
            throw new TransactionNotExistsException("Transaction with id "+ transactionId + " is already refunded");
        }
        transaction.setStatus(PaymentStatus.REFUNDED);
        Transaction refundedTransaction = transactionRepository.save(transaction);
        log.info("Transaction with transaction id={} is successfully refunded", transactionId);
        return refundedTransaction;
    }
}
