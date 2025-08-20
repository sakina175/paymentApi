package com.paymentapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor
public class Transactions {

    // public enum PaymentStatus { SUCCEEDED, FAILED, INCOMPLETE, REFUNDED }
    // public enum FailureReason { CARD_DECLINED, INSUFFICIENT_FUNDS, EXPIRED_CARD, FRAUD_DETECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customers customer;

    @Column(name = "stripe_payment_intent_id", unique = true, length = 100)
    private String stripePaymentIntentId;

    // @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private String status;

    @Column(nullable = false, length = 3)
    private String currency;

    // @Enumerated(EnumType.STRING)
    @Column(name = "failure_reason", length = 40)
    private String failureReason;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}