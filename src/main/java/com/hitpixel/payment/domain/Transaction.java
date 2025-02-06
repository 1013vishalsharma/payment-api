package com.hitpixel.payment.domain;

import com.hitpixel.payment.enums.Currency;
import com.hitpixel.payment.enums.PaymentStatus;
import com.hitpixel.payment.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Transactions")
public class Transaction implements Serializable {

    @Id
    String id;

    @Column
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column
    Currency currency;

    @ManyToOne
    User user;

    @Enumerated(EnumType.STRING)
    @Column
    PaymentStatus status;

    @Column
    LocalDateTime transactionTimestamp;

}
